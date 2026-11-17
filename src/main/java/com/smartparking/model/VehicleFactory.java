package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Factory class to instantiate polymorphic Vehicle objects based on VehicleType.
 * Demonstrates Factory Pattern and Polymorphism.
 */
public final class VehicleFactory {

    private VehicleFactory() {
        // Utility class
    }

    public static Vehicle createVehicle(VehicleType type, String regNumber, String ownerName, String phoneNumber) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        return switch (type) {
            case BIKE -> new Bike(regNumber, ownerName, phoneNumber);
            case CAR -> new Car(regNumber, ownerName, phoneNumber);
            case TRUCK -> new Truck(regNumber, ownerName, phoneNumber);
        };
    }

    public static Vehicle createVehicle(int id, VehicleType type, String regNumber, 
                                        String ownerName, String phoneNumber, 
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        return switch (type) {
            case BIKE -> new Bike(id, regNumber, ownerName, phoneNumber, createdAt, updatedAt);
            case CAR -> new Car(id, regNumber, ownerName, phoneNumber, createdAt, updatedAt);
            case TRUCK -> new Truck(id, regNumber, ownerName, phoneNumber, createdAt, updatedAt);
        };
    }
}
