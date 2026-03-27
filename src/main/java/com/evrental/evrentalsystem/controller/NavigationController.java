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

import java.security.Principal;
import java.util.Optional;

@Controller
public class NavigationController {
    
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalService rentalService;

    @GetMapping({"/vehicles", "/vehicles/"})
    public String vehicles(Model model) {
        var vehicles = vehicleRepository.findByIsAvailableTrue();
        System.out.println("DEBUG: Total vehicles found: " + vehicles.size());
        for (Vehicle v : vehicles) {
            System.out.println("DEBUG: Vehicle - ID: " + v.getId() + ", Brand: " + v.getBrand() + ", Model: " + v.getModel());
        }
        model.addAttribute("vehicles", vehicles);
        return "vehicles/list";
    }

    @GetMapping("/vehicles/search")
    public String searchVehicles(String vehicleType, String brand, double maxPrice, Model model) {
        var vehicles = vehicleRepository.findByIsAvailableTrue();

        if (vehicleType != null && !vehicleType.isEmpty()) {
            vehicles = vehicleRepository.findByVehicleTypeAndIsAvailableTrue(vehicleType);
        }

        if (brand != null && !brand.isEmpty()) {
            vehicles = vehicles.stream().filter(v -> v.getBrand().equalsIgnoreCase(brand)).toList();
        }

        if (maxPrice > 0) {
            vehicles = vehicles.stream().filter(v -> v.getPricePerHour() != null && v.getPricePerHour().doubleValue() <= maxPrice).toList();
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("selectedVehicleType", vehicleType);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("maxPrice", maxPrice);

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
