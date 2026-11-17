package com.smartparking.util;

/**
 * Thrown when no compatible parking slot is available.
 */
public class SlotUnavailableException extends ParkingException {
    public SlotUnavailableException(String message) {
        super(message);
    }
}
