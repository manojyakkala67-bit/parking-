package com.smartparking.util;

/**
 * Thrown when user input validation fails.
 */
public class ValidationException extends ParkingException {
    public ValidationException(String message) {
        super(message);
    }
}
