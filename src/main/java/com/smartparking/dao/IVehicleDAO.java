package com.smartparking.dao;

import com.smartparking.model.Vehicle;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object Interface for registered Vehicles.
 */
public interface IVehicleDAO {
    Optional<Vehicle> findById(int vehicleId);
    Optional<Vehicle> findByRegistrationNumber(String regNo);
    List<Vehicle> findAll();
    List<Vehicle> search(String keyword);
    Vehicle insert(Vehicle vehicle);
    boolean update(Vehicle vehicle);
    boolean delete(int vehicleId);
    boolean existsByRegistrationNumber(String regNo);
    boolean isVehicleCurrentlyParked(int vehicleId);
}
