package com.evrental.evrentalsystem.controller;

import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Trang dashboard admin - Hiển thị tất cả xe
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("📊 Admin Dashboard accessed");
        
        var vehicles = adminService.getAllVehicles();
        var availableCount = adminService.getAvailableVehiclesCount();
        var totalCount = adminService.getTotalVehiclesCount();
        
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("totalCount", totalCount);
        
        return "admin/dashboard";
    }

    /**
     * Trang form thêm xe mới
     */
    @GetMapping("/vehicle/new")
    public String newVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("isNew", true);
        return "admin/vehicle-form";
    }

    /**
     * Trang form chỉnh sửa xe
     */
    @GetMapping("/vehicle/{id}/edit")
    public String editVehicleForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Vehicle> vehicle = adminService.getVehicleById(id);
        
        if (vehicle.isPresent()) {
            model.addAttribute("vehicle", vehicle.get());
            model.addAttribute("isNew", false);
            return "admin/vehicle-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Xe không tồn tại!");
            return "redirect:/admin/dashboard";
        }
    }

    /**
     * Lưu xe mới (POST)
     */
    @PostMapping("/vehicle/save")
    public String saveVehicle(
            @ModelAttribute Vehicle vehicle,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("📝 Saving vehicle: {}", vehicle.getBrand());
            
            // Log file info
            if (imageFile != null && !imageFile.isEmpty()) {
                log.info("📸 Image file received: {}, size: {} bytes", imageFile.getOriginalFilename(), imageFile.getSize());
            } else {
                log.info("⚠️ No image file provided");
            }
            
            // Xác thực dữ liệu
            if (vehicle.getBrand() == null || vehicle.getBrand().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập thương hiệu!");
                return "redirect:/admin/vehicle/new";
            }

            if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
                // Tạo xe mới
                Vehicle newVehicle = adminService.createVehicle(vehicle, imageFile);
                redirectAttributes.addFlashAttribute("success", "✅ Xe được thêm thành công!");
                log.info("✅ New vehicle created: {}", newVehicle.getId());
            } else {
                // Cập nhật xe
                Vehicle updatedVehicle = adminService.updateVehicle(vehicle.getId(), vehicle, imageFile);
                redirectAttributes.addFlashAttribute("success", "✅ Xe được cập nhật thành công!");
                log.info("✅ Vehicle updated: {}", vehicle.getId());
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Lỗi: " + e.getMessage());
            log.error("❌ Error saving vehicle: {}", e.getMessage(), e);
        }

        return "redirect:/admin/dashboard";
    }

    /**
     * Xóa xe
     */
    @PostMapping("/vehicle/{id}/delete")
    public String deleteVehicle(
            @PathVariable String id,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("success", "✅ Xe được xóa thành công!");
            log.info("✅ Vehicle deleted: {}", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Lỗi xóa xe: " + e.getMessage());
            log.error("Error deleting vehicle: {}", e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    /**
     * API: Lấy xe theo ID (JSON)
     */
    @GetMapping("/api/vehicle/{id}")
    @ResponseBody
    public Optional<Vehicle> getVehicle(@PathVariable String id) {
        return adminService.getVehicleById(id);
    }

    /**
     * API: Xóa xe nhanh (JSON)
     */
    @DeleteMapping("/api/vehicle/{id}")
    @ResponseBody
    public String deleteVehicleApi(@PathVariable String id) {
        try {
            adminService.deleteVehicle(id);
            return "{\"success\": true, \"message\": \"Xe xóa thành công\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
}
