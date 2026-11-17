package com.smartparking.util;

/**
 * Thrown when attempting to park a vehicle that is already actively parked in a slot.
 */
public class DuplicateVehicleException extends ParkingException {
    public DuplicateVehicleException(String message) {
        super(message);
    }
}
