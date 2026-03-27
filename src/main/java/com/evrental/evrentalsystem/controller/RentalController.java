package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.entity.Rental;
import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.UserRepository;
import com.evrental.evrentalsystem.repository.VehicleRepository;
import com.evrental.evrentalsystem.service.RentalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/booking/create")
    @PreAuthorize("isAuthenticated()")
    public String bookingForm(@RequestParam(name = "vehicleId", required = false) String vehicleId,
                              Model model) {
        if (vehicleId != null) {
            vehicleRepository.findById(vehicleId).ifPresent(v -> model.addAttribute("vehicle", v));
        }
        return "booking/create";
    }

    // API endpoint để validate booking
    @PostMapping("/api/booking/validate")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateBooking(
            @RequestParam String vehicleId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Principal principal) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("🔍 Validating booking: vehicleId={}, startDate={}, endDate={}, user={}", 
                vehicleId, startDate, endDate, principal.getName());
            
            // Check vehicle exists
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));
            
            if (!vehicle.getIsAvailable()) {
                response.put("valid", false);
                response.put("message", "Xe không còn khả dụng");
                response.put("pricePerHour", vehicle.getPricePerHour());
                return ResponseEntity.ok(response);
            }
            
            // Check user exists
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            
            // Parse dates
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            // Validate dates
            if (start.isAfter(end) || start.isBefore(LocalDateTime.now())) {
                response.put("valid", false);
                response.put("message", "Ngày bắt đầu phải sau ngày hôm nay và trước ngày kết thúc");
                response.put("pricePerHour", vehicle.getPricePerHour());
                return ResponseEntity.ok(response);
            }
            
            // Calculate price
            long hours = Duration.between(start, end).toHours();
            if (hours == 0) hours = 1;
            
            BigDecimal totalPrice = vehicle.getPricePerHour()
                    .multiply(BigDecimal.valueOf(hours));
            
            log.info("✅ Booking validation successful: hours={}, totalPrice={}", hours, totalPrice);
            
            response.put("valid", true);
            response.put("message", "Đặt xe thành công!");
            response.put("hours", hours);
            response.put("pricePerHour", vehicle.getPricePerHour());
            response.put("totalPrice", totalPrice);
            response.put("vehicleName", vehicle.getBrand() + " " + vehicle.getModel());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Booking validation error", e);
            response.put("valid", false);
            response.put("message", "Lỗi kiểm tra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/booking/create")
    @PreAuthorize("isAuthenticated()")
    public Object createBooking(String vehicleId,
                                String startDate,
                                String endDate,
                                String notes,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        String email = principal.getName();
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("📝 Creating booking: vehicleId={}, startDate={}, endDate={}, user={}", 
                vehicleId, startDate, endDate, email);
            
            // Validate inputs
            if (vehicleId == null || vehicleId.isEmpty()) {
                throw new IllegalArgumentException("ID xe không hợp lệ");
            }
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu và kết thúc");
            }
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại: " + email));

            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại: " + vehicleId));

            if (!vehicle.getIsAvailable()) {
                throw new RuntimeException("Xe không còn khả dụng");
            }

            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            // Create rental request
            Rental rental = rentalService.createRentalRequest(user, vehicle, start, end, notes);
            
            response.put("success", true);
            response.put("message", "✅ Yêu cầu thuê xe gửi thành công! Vui lòng chờ Admin duyệt.");
            response.put("rentalId", rental.getId());
            
            log.info("✅ Booking created successfully: rentalId={}", rental.getId());
            
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Validation error: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "❌ Lỗi: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error creating booking", e);
            response.put("success", false);
            response.put("message", "❌ Lỗi khi gửi yêu cầu: " + e.getMessage());
        }

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }

        if ((boolean) response.getOrDefault("success", false)) {
            redirectAttributes.addFlashAttribute("success", response.get("message"));
        } else {
            redirectAttributes.addFlashAttribute("error", response.get("message"));
        }

        return "redirect:/vehicles/" + vehicleId;
    }

    @GetMapping("/my-rentals")
    @PreAuthorize("isAuthenticated()")
    public String myRentals(Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        List<Rental> rentals = rentalService.getRentalsForUser(user.getId());
        model.addAttribute("rentals", rentals);
        return "user/my-rentals";
    }

    @PostMapping("/vehicles/{vehicleId}/rate")
    @PreAuthorize("isAuthenticated()")
    public String rateVehicle(@PathVariable String vehicleId,
                              int rating,
                              RedirectAttributes redirectAttributes) {
        try {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
            }
            rentalService.addRating(vehicleId, rating);
            redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehicles/" + vehicleId;
    }

    @GetMapping("/admin/rentals")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminRentals(Model model) {
        List<Rental> pending = rentalService.getRentalsByStatus("PENDING");
        List<Rental> confirmed = rentalService.getRentalsByStatus("CONFIRMED");
        List<Rental> rejected = rentalService.getRentalsByStatus("REJECTED");

        model.addAttribute("pending", pending);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("rejected", rejected);

        return "admin/rentals";
    }

    @GetMapping("/admin/rentals/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminRentalDetail(@PathVariable String id, Model model) {
        Rental rental = rentalService.getRentalById(id)
                .orElseThrow(() -> new RuntimeException("Rental không tồn tại: " + id));
        model.addAttribute("rental", rental);
        return "admin/rental-detail";
    }

    @PostMapping("/admin/rentals/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRental(@PathVariable String id, RedirectAttributes redirectAttributes) {
        rentalService.approveRental(id);
        redirectAttributes.addFlashAttribute("success", "Đã duyệt yêu cầu thuê xe");
        return "redirect:/admin/rentals";
    }

    @PostMapping("/admin/rentals/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectRental(@PathVariable String id, RedirectAttributes redirectAttributes) {
        rentalService.rejectRental(id);
        redirectAttributes.addFlashAttribute("success", "Đã từ chối yêu cầu thuê xe");
        return "redirect:/admin/rentals";
    }
}