package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Concrete Vehicle subclass representing a Four-Wheeler / Car.
 * Rate: ₹100 per hour.
 */
public class Car extends Vehicle {
    public static final double HOURLY_RATE = 100.0;

    public Car() {
        super();
        setVehicleType(VehicleType.CAR);
    }

    public Car(String registrationNumber, String ownerName, String phoneNumber) {
        super(registrationNumber, VehicleType.CAR, ownerName, phoneNumber);
    }

    public Car(int vehicleId, String registrationNumber, String ownerName, 
               String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(vehicleId, registrationNumber, VehicleType.CAR, ownerName, phoneNumber, createdAt, updatedAt);
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
