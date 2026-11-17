package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Concrete Vehicle subclass representing a Heavy Vehicle / Truck.
 * Rate: ₹150 per hour.
 */
public class Truck extends Vehicle {
    public static final double HOURLY_RATE = 150.0;

    public Truck() {
        super();
        setVehicleType(VehicleType.TRUCK);
    }

    public Truck(String registrationNumber, String ownerName, String phoneNumber) {
        super(registrationNumber, VehicleType.TRUCK, ownerName, phoneNumber);
    }

    public Truck(int vehicleId, String registrationNumber, String ownerName, 
                 String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(vehicleId, registrationNumber, VehicleType.TRUCK, ownerName, phoneNumber, createdAt, updatedAt);
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
