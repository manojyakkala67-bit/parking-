package com.smartparking.dao;

import com.smartparking.dao.storage.DataStore;
import com.smartparking.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Registered Vehicles (Standalone In-Memory & Local Storage).
 */
public class VehicleDAOImpl implements IVehicleDAO {

    private final DataStore store = DataStore.getInstance();

    @Override
    public Optional<Vehicle> findById(int vehicleId) {
        return store.findVehicleById(vehicleId);
    }

    @Override
    public Optional<Vehicle> findByRegistrationNumber(String regNo) {
        return store.findVehicleByRegNo(regNo);
    }

    @Override
    public List<Vehicle> findAll() {
        return store.findAllVehicles();
    }

    @Override
    public List<Vehicle> search(String keyword) {
        return store.searchVehicles(keyword);
    }

    @Override
    public Vehicle insert(Vehicle vehicle) {
        return store.insertVehicle(vehicle);
    }

    @Override
    public boolean update(Vehicle vehicle) {
        return store.updateVehicle(vehicle);
    }

    @Override
    public boolean delete(int vehicleId) {
        return store.deleteVehicle(vehicleId);
    }

    @Override
    public boolean existsByRegistrationNumber(String regNo) {
        return store.findVehicleByRegNo(regNo).isPresent();
    }

    @Override
    public boolean isVehicleCurrentlyParked(int vehicleId) {
        return store.isVehicleCurrentlyParked(vehicleId);
    }
}
