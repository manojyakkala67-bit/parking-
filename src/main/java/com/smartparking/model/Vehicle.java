package com.smartparking.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class representing a Vehicle in the parking management system.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Vehicle {
    private int vehicleId;
    private String registrationNumber;
    private VehicleType vehicleType;
    private String ownerName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vehicle() {
    }

    public Vehicle(String registrationNumber, VehicleType vehicleType, String ownerName, String phoneNumber) {
        setRegistrationNumber(registrationNumber);
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        setOwnerName(ownerName);
        setPhoneNumber(phoneNumber);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Vehicle(int vehicleId, String registrationNumber, VehicleType vehicleType, 
                   String ownerName, String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Polymorphic method to calculate parking fee based on duration in hours.
     * Minimum charge is for 1 hour. Started hours are rounded up.
     *
     * @param durationHours duration in hours (e.g. 1, 2, 3...)
     * @return total fee in Rupees
     */
    public abstract double calculateFee(long durationHours);

    /**
     * Returns the hourly rate for this vehicle category.
     *
     * @return hourly rate in INR
     */
    public abstract double getHourlyRate();

    // Getters and Setters with Encapsulation & Validation

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty");
        }
        this.ownerName = ownerName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        this.phoneNumber = phoneNumber.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return Objects.equals(registrationNumber, vehicle.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s (%s)", registrationNumber, vehicleType, ownerName, phoneNumber);
    }
}
