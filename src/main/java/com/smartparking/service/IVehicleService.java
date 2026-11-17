package com.smartparking.service;

import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Vehicle Registry operations.
 */
public interface IVehicleService {
    Vehicle registerVehicle(String regNo, VehicleType type, String ownerName, String phone);
    Vehicle updateVehicle(int vehicleId, String regNo, VehicleType type, String ownerName, String phone);
    boolean deleteVehicle(int vehicleId);
    Optional<Vehicle> getVehicleByRegNo(String regNo);
    List<Vehicle> getAllVehicles();
    List<Vehicle> searchVehicles(String keyword);
}
