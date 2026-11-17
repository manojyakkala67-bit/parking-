package com.smartparking.util;

/**
 * Base custom exception for the Smart Parking Management System.
 */
public class ParkingException extends RuntimeException {
    public ParkingException(String message) {
        super(message);
    }

    public ParkingException(String message, Throwable cause) {
        super(message, cause);
    }
}
