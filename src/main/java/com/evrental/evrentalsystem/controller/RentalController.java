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
public String bookingForm(@RequestParam(name = "vehicleId") String vehicleId, Model model) {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + vehicleId));
    model.addAttribute("vehicle", vehicle);
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
    Principal principal){
    
    Map<String, Object> response = new HashMap<>();
    
    try {
        // 1. Kiểm tra đầu vào cơ bản
        if (startDate.isEmpty() || endDate.isEmpty()) {
            response.put("valid", false);
            response.put("message", "Vui lòng chọn đầy đủ thời gian");
            return ResponseEntity.ok(response);
        }

        // 2. Kiểm tra Xe
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(null);
        if (vehicle == null) {
            response.put("valid", false);
            response.put("message", "Xe không tồn tại trong hệ thống");
            return ResponseEntity.ok(response);
        }

        // 3. Parse ngày tháng an toàn
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        // 4. Kiểm tra logic ngày tháng
        if (start.isAfter(end) || start.isBefore(LocalDateTime.now().minusMinutes(5))) {
            response.put("valid", false);
            response.put("message", "Thời gian đặt xe không hợp lệ");
            return ResponseEntity.ok(response);
        }
        
        // 5. Tính toán (Sử dụng giá trị mặc định nếu null)
        long hours = Duration.between(start, end).toHours();
        if (hours <= 0) hours = 1;
        
        BigDecimal price = vehicle.getPricePerHour() != null ? vehicle.getPricePerHour() : BigDecimal.ZERO;
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(hours));
        
        response.put("valid", true);
        response.put("hours", hours);
        response.put("totalPrice", totalPrice);
        response.put("message", "Hợp lệ");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        log.error("Lỗi validate: ", e);
        response.put("valid", false);
        response.put("message", "Lỗi hệ thống: " + e.getMessage());
        return ResponseEntity.ok(response); // Trả về 200 kèm message lỗi thay vì trả về 500
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
public String getMyRentals(Model model, Principal principal) {
    if (principal == null) return "redirect:/login";

    // 1. Lấy email từ người dùng đang đăng nhập, rồi tìm User trong DB
    User currentUser = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2. Lấy danh sách thuê xe của đúng User đó
    List<Rental> userRentals = rentalService.findByUser(currentUser);

    // 3. Gửi dữ liệu sang HTML
    model.addAttribute("rentals", userRentals);

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