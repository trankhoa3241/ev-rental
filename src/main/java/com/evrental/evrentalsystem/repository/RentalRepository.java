package com.evrental.evrentalsystem.repository;

import com.evrental.evrentalsystem.entity.Rental;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends MongoRepository<Rental, String> {
    List<Rental> findByUserId(String userId);
    List<Rental> findByVehicleId(String vehicleId);
    List<Rental> findByStatus(String status);
}
