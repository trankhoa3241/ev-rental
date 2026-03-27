package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.UserRepository;
import com.evrental.evrentalsystem.repository.VehicleRepository;
import com.evrental.evrentalsystem.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;
import java.util.List;

@Controller
public class NavigationController {
    
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalService rentalService;

    

    @GetMapping("/vehicles/search")
public String searchVehicles(
    @RequestParam(required = false) String vehicleType, 
    @RequestParam(required = false) String brand, 
    @RequestParam(required = false) Double maxPrice, 
    Model model) {
    
    // ĐỔI TẠI ĐÂY: Lấy tất cả xe thay vì chỉ xe Available
    var vehicles = vehicleRepository.findAll(); 

    if (vehicleType != null && !vehicleType.isEmpty()) {
        // Nếu Repo có hàm findByVehicleType thì dùng, nếu không thì dùng stream để lọc
        vehicles = vehicles.stream()
                .filter(v -> v.getVehicleType().equalsIgnoreCase(vehicleType))
                .toList();
    }

    if (brand != null && !brand.isEmpty()) {
        vehicles = vehicles.stream()
                .filter(v -> v.getBrand() != null && v.getBrand().equalsIgnoreCase(brand))
                .toList();
    }

    if (maxPrice != null && maxPrice > 0) {
        vehicles = vehicles.stream()
                .filter(v -> v.getPricePerHour() != null && v.getPricePerHour().doubleValue() <= maxPrice)
                .toList();
    }

    model.addAttribute("vehicles", vehicles);
    return "vehicles/search-results"; 
}

    @GetMapping("/vehicles/{id}")
    public String vehicleDetail(@PathVariable String id, Model model) {
        System.out.println("DEBUG: Fetching vehicle with id: " + id);
        
        if (id == null || id.isEmpty() || id.equals("/")) {
            System.out.println("DEBUG: ID is empty or /, redirecting to vehicles list");
            return "redirect:/vehicles";
        }
        
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                System.out.println("DEBUG: Vehicle found: " + vehicle.get().getBrand() + " " + vehicle.get().getModel());
                model.addAttribute("vehicle", vehicle.get());
                return "vehicles/detail";
            } else {
                System.out.println("DEBUG: Vehicle not found with id: " + id);
                return "redirect:/vehicles";
            }
        } catch (Exception e) {
            System.err.println("ERROR: Exception in vehicleDetail: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/vehicles";
        }
    }
    // CHỈ GIỮ LẠI MỘT HÀM NÀY CHO TRANG /vehicles
    @GetMapping({"/vehicles", "/vehicles/"})
    public String showVehicleList(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        // 1. Logic lấy dữ liệu: Mặc định lấy TẤT CẢ xe
        List<Vehicle> vehicles = vehicleRepository.findAll(); 

        // 2. Thực hiện lọc (Chỉ lọc nếu người dùng có chọn điều kiện)
        if (vehicleType != null && !vehicleType.isEmpty()) {
            vehicles = vehicles.stream()
            .filter(v -> v.getVehicleType() != null && v.getVehicleType().equalsIgnoreCase(vehicleType))
            .toList();
        }

        if (brand != null && !brand.isEmpty()) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getBrand() != null && v.getBrand().equalsIgnoreCase(brand))
                    .toList();
        }

        if (maxPrice != null && maxPrice > 0) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getPricePerHour() != null && v.getPricePerHour().doubleValue() <= maxPrice)
                    .toList();
        }

        // 3. Đưa dữ liệu ra trang list.html
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("selectedType", vehicleType);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("currentMaxPrice", maxPrice);

        return "vehicles/list";
    }

    // Hàm searchVehicles của bạn giữ nguyên bên dưới, không sửa gì cả

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        var rentals = rentalService.getRentalsForUser(user.getId());
        long pending = rentals.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long confirmed = rentals.stream().filter(r -> "CONFIRMED".equals(r.getStatus())).count();
        long completed = rentals.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();

        long total = rentals.size();
        java.math.BigDecimal spent = rentals.stream()
                .filter(r -> r.getTotalPrice() != null)
                .map(r -> r.getTotalPrice())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("pending", pending);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("completed", completed);
        model.addAttribute("total", total);
        model.addAttribute("spent", spent);

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }

}
