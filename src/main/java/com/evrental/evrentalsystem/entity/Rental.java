package com.evrental.evrentalsystem.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rentals")
public class Rental {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private Vehicle vehicle;

    private LocalDateTime pickupDateTime;

    private LocalDateTime returnDateTime;

    private LocalDateTime actualReturnDateTime;

    private String pickupLocation;

    private String returnLocation;

    private BigDecimal totalPrice;

    private String status; // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED

    private String paymentStatus; // PENDING, COMPLETED, FAILED, REFUNDED

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
