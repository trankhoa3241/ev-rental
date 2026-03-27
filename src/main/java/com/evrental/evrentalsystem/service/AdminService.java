package com.evrental.evrentalsystem.service;

import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private RentalService rentalService;

    /**
     * Lấy tất cả xe
     */
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        log.info("✅ Retrieved {} vehicles", vehicles.size());
        return vehicles;
    }

    /**
     * Lấy xe theo ID
     */
    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    /**
     * Tạo xe mới
     */
    public Vehicle createVehicle(Vehicle vehicle, MultipartFile imageFile) {
        try {
            log.info("🆕 Creating new vehicle: {}", vehicle.getBrand());
            
            // ⭐ IMPORTANT: Generate UUID for new vehicle
            if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
                vehicle.setId(UUID.randomUUID().toString());
                log.info("📌 Generated ID for new vehicle: {}", vehicle.getId());
            }
            
            // Set timestamps
            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());
            
            // Upload ảnh lên Cloudinary nếu có file
            if (imageFile != null && !imageFile.isEmpty()) {
                log.info("📸 Processing image upload: {}, size: {}", imageFile.getOriginalFilename(), imageFile.getSize());
                String imageUrl = cloudinaryService.uploadFile(imageFile, "ev-rental/vehicles");
                vehicle.setImageUrl(imageUrl);
                log.info("✅ Image uploaded successfully: {}", imageUrl);
            } else {
                log.warn("⚠️ No image provided, using default placeholder");
                vehicle.setImageUrl("https://via.placeholder.com/500x400?text=" + 
                                  vehicle.getBrand() + "+" + vehicle.getModel());
            }

            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            log.info("✅ Vehicle created successfully: ID={}, Brand={} {}", 
                    savedVehicle.getId(), savedVehicle.getBrand(), savedVehicle.getModel());
            return savedVehicle;

        } catch (Exception e) {
            log.error("❌ Error creating vehicle: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create vehicle: " + e.getMessage());
        }
    }

    /**
     * Cập nhật xe
     */
    public Vehicle updateVehicle(String id, Vehicle vehicleDetails, MultipartFile imageFile) {
        try {
            log.info("✏️ Updating vehicle: {}", id);
            
            Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);

            if (existingVehicle.isPresent()) {
                Vehicle vehicle = existingVehicle.get();

                // Cập nhật thông tin cơ bản
                vehicle.setBrand(vehicleDetails.getBrand());
                vehicle.setModel(vehicleDetails.getModel());
                vehicle.setVehicleType(vehicleDetails.getVehicleType());
                vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
                vehicle.setBatteryCapacity(vehicleDetails.getBatteryCapacity());
                vehicle.setMaxRange(vehicleDetails.getMaxRange());
                vehicle.setPricePerHour(vehicleDetails.getPricePerHour());
                vehicle.setPricePerDay(vehicleDetails.getPricePerDay());
                vehicle.setCurrentCharge(vehicleDetails.getCurrentCharge());
                vehicle.setCurrentLocation(vehicleDetails.getCurrentLocation());
                vehicle.setDescription(vehicleDetails.getDescription());
                vehicle.setIsAvailable(vehicleDetails.getIsAvailable());

                // Cập nhật ảnh nếu có file mới
                if (imageFile != null && !imageFile.isEmpty()) {
                    log.info("📸 Processing new image upload: {}, size: {}", imageFile.getOriginalFilename(), imageFile.getSize());
                    String imageUrl = cloudinaryService.uploadFile(imageFile, "ev-rental/vehicles");
                    vehicle.setImageUrl(imageUrl);
                    log.info("✅ Image updated successfully: {}", imageUrl);
                } else {
                    log.info("⚠️ No new image provided, keeping existing image");
                    // Giữ nguyên ảnh cũ
                    if (vehicleDetails.getImageUrl() != null && !vehicleDetails.getImageUrl().isEmpty()) {
                        vehicle.setImageUrl(vehicleDetails.getImageUrl());
                    }
                }

                Vehicle updatedVehicle = vehicleRepository.save(vehicle);
                log.info("✅ Vehicle updated successfully: {}", id);
                return updatedVehicle;
            } else {
                log.error("❌ Vehicle not found with id: {}", id);
                throw new RuntimeException("Vehicle not found with id: " + id);
            }

        } catch (Exception e) {
            log.error("❌ Error updating vehicle: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update vehicle: " + e.getMessage());
        }
    }

    /**
     * Xóa xe
     */
    public void deleteVehicle(String id) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                vehicleRepository.deleteById(id);
                log.info("✅ Vehicle deleted successfully: {}", id);
            } else {
                throw new RuntimeException("Vehicle not found with id: " + id);
            }
        } catch (Exception e) {
            log.error("❌ Error deleting vehicle: {}", e.getMessage());
            throw new RuntimeException("Failed to delete vehicle: " + e.getMessage());
        }
    }

    /**
     * Tìm xe theo loại
     */
    public List<Vehicle> getVehiclesByType(String vehicleType) {
        return vehicleRepository.findByVehicleType(vehicleType);
    }

    /**
     * Tìm xe theo thương hiệu
     */
    public List<Vehicle> getVehiclesByBrand(String brand) {
        return vehicleRepository.findByBrand(brand);
    }

    /**
     * Thống kê xe có sẵn
     */
    public long getAvailableVehiclesCount() {
        return vehicleRepository.findByIsAvailableTrue().stream().count();
    }

    /**
     * Thống kê tổng số xe
     */
    public long getTotalVehiclesCount() {
        return vehicleRepository.count();
    }

    /**
     * Top xe được thuê nhiều nhất
     */
    public List<Map.Entry<Vehicle, Long>> getTopRentedVehicles(int limit) {
        return rentalService.getTopRentedVehicles(limit);
    }
}
