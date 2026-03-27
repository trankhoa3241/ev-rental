package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.repository.VehicleRepository;
import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
public String index(Model model) {
    // 1. Gọi hàm lấy dữ liệu
    List<Vehicle> featuredVehicles = vehicleRepository.findTop6ByOrderByRatingsDesc();

    // 2. DÒNG NÀY ĐỂ KIỂM TRA (Nhìn vào Console của IntelliJ/VS Code khi chạy)
    System.out.println("---------- DEBUG VEHICLES ----------");
    if (featuredVehicles == null || featuredVehicles.isEmpty()) {
        System.out.println("KẾT QUẢ: KHÔNG lấy được xe nào từ Database!");
    } else {
        System.out.println("KẾT QUẢ: Lấy được " + featuredVehicles.size() + " chiếc xe.");
        featuredVehicles.forEach(v -> System.out.println(" - Xe: " + v.getBrand() + " " + v.getModel()));
    }
    System.out.println("------------------------------------");

    model.addAttribute("featuredVehicles", featuredVehicles);
    model.addAttribute("totalVehicles", vehicleRepository.count());

    return "index";
}

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/api/debug/users")
    @ResponseBody
    public Map<String, Object> debugUsers() {
        Map<String, Object> response = new HashMap<>();
        List<?> allUsers = userRepository.findAll();
        response.put("totalUsers", allUsers.size());
        response.put("message", "DEBUG ENDPOINT - Shows all users in MongoDB");
        response.put("users", allUsers);
        return response;
    }

    @GetMapping("/api/debug/users-by-email/{email}")
    @ResponseBody
    public Map<String, Object> debugUserByEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        var user = userRepository.findByEmail(email);
        response.put("found", user.isPresent());
        response.put("email", email);
        if (user.isPresent()) {
            response.put("user", user.get());
        } else {
            response.put("message", "No user found with this email in MongoDB");
        }
        return response;
    }
}
