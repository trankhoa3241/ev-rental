package com.evrental.evrentalsystem.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;

    private String brand; // Honda, Vespa, Tesla, etc.

    private String model;

    private String vehicleType; // MOTORCYCLE, CAR

    private String licensePlate;

    private Integer batteryCapacity; // in kWh

    private Integer maxRange; // in km

    private BigDecimal pricePerHour;

    private BigDecimal pricePerDay;

    private Integer currentCharge; // percentage 0-100

    private String currentLocation; // address or pickup point

    private String imageUrl;

    private String description;

    private Boolean isAvailable = true;

    private Double ratings = 0.0;

    private Integer totalReviews = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
