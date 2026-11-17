package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Concrete Vehicle subclass representing a Two-Wheeler / Bike.
 * Rate: ₹40 per hour.
 */
public class Bike extends Vehicle {
    public static final double HOURLY_RATE = 40.0;

    public Bike() {
        super();
        setVehicleType(VehicleType.BIKE);
    }

    public Bike(String registrationNumber, String ownerName, String phoneNumber) {
        super(registrationNumber, VehicleType.BIKE, ownerName, phoneNumber);
    }

    public Bike(int vehicleId, String registrationNumber, String ownerName, 
                String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(vehicleId, registrationNumber, VehicleType.BIKE, ownerName, phoneNumber, createdAt, updatedAt);
    }

    @Override
    public double calculateFee(long durationHours) {
        long billableHours = Math.max(1, durationHours);
        return billableHours * HOURLY_RATE;
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }
}
