package com.smartparking.util;

/**
 * Thrown when admin authentication fails due to invalid credentials.
 */
public class InvalidCredentialsException extends ParkingException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
