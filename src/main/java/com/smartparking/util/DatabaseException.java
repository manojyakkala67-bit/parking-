package com.smartparking.util;

/**
 * Thrown when an underlying JDBC or database error occurs.
 */
public class DatabaseException extends ParkingException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
