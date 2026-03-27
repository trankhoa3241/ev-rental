package com.evrental.evrentalsystem.service;

import com.evrental.evrentalsystem.entity.Rental;
import com.evrental.evrentalsystem.entity.User;
import com.evrental.evrentalsystem.entity.Vehicle;
import com.evrental.evrentalsystem.repository.RentalRepository;
import com.evrental.evrentalsystem.repository.UserRepository;
import com.evrental.evrentalsystem.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    public Rental createRentalRequest(User user, Vehicle vehicle, LocalDateTime pickupDateTime, LocalDateTime returnDateTime, String notes) {
        if (pickupDateTime.isAfter(returnDateTime) || pickupDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày bắt đầu không hợp lệ");
        }

        long hours = Math.max(1, Duration.between(pickupDateTime, returnDateTime).toHours());
        BigDecimal totalPrice = vehicle.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVehicle(vehicle);
        rental.setPickupDateTime(pickupDateTime);
        rental.setReturnDateTime(returnDateTime);
        rental.setActualReturnDateTime(null);
        rental.setPickupLocation(vehicle.getCurrentLocation());
        rental.setReturnLocation(vehicle.getCurrentLocation());
        rental.setTotalPrice(totalPrice);
        rental.setStatus("PENDING");
        rental.setPaymentStatus("PENDING");
        rental.setNotes(notes);
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());

        Rental saved = rentalRepository.save(rental);
        log.info("✅ Rental request created (PENDING): {} for user {}", saved.getId(), user.getEmail());
        return saved;
    }

    public List<Rental> getRentalsForUser(String userId) {
        return rentalRepository.findByUserId(userId);
    }

    public Optional<Rental> getRentalById(String id) {
        return rentalRepository.findById(id);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByStatus(String status) {
        return rentalRepository.findByStatus(status);
    }

    public void approveRental(String rentalId) {
        updateRentalStatus(rentalId, "CONFIRMED");
    }

    public void rejectRental(String rentalId) {
        updateRentalStatus(rentalId, "REJECTED");
    }

    public void updateRentalStatus(String rentalId, String status) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + rentalId));

        if (!"PENDING".equals(rental.getStatus())) {
            throw new RuntimeException("Chỉ có yêu cầu đang xử lý mới được duyệt hoặc từ chối.");
        }

        rental.setStatus(status);
        rental.setUpdatedAt(LocalDateTime.now());

        if ("CONFIRMED".equals(status)) {
            Vehicle v = rental.getVehicle();
            v.setIsAvailable(false);
            vehicleRepository.save(v);
            rental.setPaymentStatus("COMPLETED");
        }

        if ("REJECTED".equals(status)) {
            rental.setPaymentStatus("FAILED");
        }

        rentalRepository.save(rental);
        log.info("✅ Rental {} status updated to {}", rentalId, status);
    }

    public List<Map.Entry<Vehicle, Long>> getTopRentedVehicles(int limit) {
        Map<String, Long> counts = rentalRepository.findAll().stream()
                .filter(r -> "CONFIRMED".equals(r.getStatus()) || "COMPLETED".equals(r.getStatus()))
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId(), Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> {
                    Optional<Vehicle> vehicle = vehicleRepository.findById(entry.getKey());
                    return vehicle.map(v -> Map.entry(v, entry.getValue()));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.<Map.Entry<Vehicle, Long>>comparingLong(Map.Entry::getValue).reversed())
                .limit(limit)
                .toList();
    }

    public void addRating(String vehicleId, double stars) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        double existingRating = vehicle.getRatings() != null ? vehicle.getRatings() : 0.0;
        int existingReviews = vehicle.getTotalReviews() != null ? vehicle.getTotalReviews() : 0;

        double average = (existingRating * existingReviews + stars) / (existingReviews + 1);
        vehicle.setRatings(Math.round(average * 10.0) / 10.0);
        vehicle.setTotalReviews(existingReviews + 1);
        vehicleRepository.save(vehicle);
    }
}