package com.smartparking.service;

import com.smartparking.dao.IVehicleDAO;
import com.smartparking.dao.VehicleDAOImpl;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleFactory;
import com.smartparking.model.VehicleType;
import com.smartparking.util.DuplicateVehicleException;
import com.smartparking.util.ParkingException;
import com.smartparking.util.ValidationException;
import com.smartparking.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of IVehicleService.
 */
public class VehicleServiceImpl implements IVehicleService {

    private final IVehicleDAO vehicleDAO;

    public VehicleServiceImpl() {
        this.vehicleDAO = new VehicleDAOImpl();
    }

    public VehicleServiceImpl(IVehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    @Override
    public Vehicle registerVehicle(String regNo, VehicleType type, String ownerName, String phone) {
        ValidationUtil.validateRegistrationNumber(regNo);
        ValidationUtil.validateOwnerName(ownerName);
        ValidationUtil.validatePhoneNumber(phone);
        if (type == null) {
            throw new ValidationException("Vehicle type must be selected.");
        }

        String normalizedReg = regNo.trim().toUpperCase();
        if (vehicleDAO.existsByRegistrationNumber(normalizedReg)) {
            throw new DuplicateVehicleException("A vehicle with registration number " + normalizedReg + " is already registered.");
        }

        Vehicle vehicle = VehicleFactory.createVehicle(type, normalizedReg, ownerName.trim(), phone.trim());
        return vehicleDAO.insert(vehicle);
    }

    @Override
    public Vehicle updateVehicle(int vehicleId, String regNo, VehicleType type, String ownerName, String phone) {
        ValidationUtil.validateRegistrationNumber(regNo);
        ValidationUtil.validateOwnerName(ownerName);
        ValidationUtil.validatePhoneNumber(phone);
        if (type == null) {
            throw new ValidationException("Vehicle type must be selected.");
        }

        Optional<Vehicle> existingOpt = vehicleDAO.findById(vehicleId);
        if (existingOpt.isEmpty()) {
            throw new ParkingException("Vehicle not found with ID: " + vehicleId);
        }

        Vehicle vehicle = existingOpt.get();
        vehicle.setRegistrationNumber(regNo.trim().toUpperCase());
        vehicle.setVehicleType(type);
        vehicle.setOwnerName(ownerName.trim());
        vehicle.setPhoneNumber(phone.trim());

        boolean ok = vehicleDAO.update(vehicle);
        if (!ok) {
            throw new ParkingException("Failed to update vehicle record.");
        }
        return vehicle;
    }

    @Override
    public boolean deleteVehicle(int vehicleId) {
        if (vehicleDAO.isVehicleCurrentlyParked(vehicleId)) {
            throw new ParkingException("Cannot delete vehicle record: the vehicle is currently parked in an active slot.");
        }
        return vehicleDAO.delete(vehicleId);
    }

    @Override
    public Optional<Vehicle> getVehicleByRegNo(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return vehicleDAO.findByRegistrationNumber(regNo.trim().toUpperCase());
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.findAll();
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllVehicles();
        }
        return vehicleDAO.search(keyword.trim());
    }
}
