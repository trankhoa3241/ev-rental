package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.repository.VehicleRepository;
import com.evrental.evrentalsystem.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@Controller
public class NavigationController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
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
        var vehicles = vehicleRepository.findAll();
        if (vehicleType != null && !vehicleType.isEmpty()) {
            vehicles = vehicles.stream().filter(v -> v.getVehicleType().equals(vehicleType)).toList();
        }
        if (brand != null && !brand.isEmpty()) {
            vehicles = vehicles.stream().filter(v -> v.getBrand().equals(brand)).toList();
        }
        if (maxPrice > 0) {
            vehicles = vehicles.stream().filter(v -> v.getPricePerHour().doubleValue() <= maxPrice).toList();
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
        
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            System.out.println("DEBUG: Vehicle found: " + vehicle.get().getBrand() + " " + vehicle.get().getModel());
            model.addAttribute("vehicle", vehicle.get());
            return "vehicles/detail";
        } else {
            System.out.println("DEBUG: Vehicle not found with id: " + id);
            return "redirect:/vehicles";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }

    @GetMapping("/my-rentals")
    public String myRentals() {
        return "user/my-rentals";
    }
}
