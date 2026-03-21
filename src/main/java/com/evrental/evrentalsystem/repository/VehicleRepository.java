package com.evrental.evrentalsystem.repository;

import com.evrental.evrentalsystem.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    List<Vehicle> findByIsAvailableTrue();
    List<Vehicle> findByVehicleType(String vehicleType);
    List<Vehicle> findByBrand(String brand);
}
