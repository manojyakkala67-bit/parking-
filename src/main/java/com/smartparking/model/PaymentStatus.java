package com.smartparking.model;

/**
 * Enum representing payment status.
 */
public enum PaymentStatus {
    SUCCESS("Success"),
    PENDING("Pending"),
    FAILED("Failed");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromString(String text) {
        if (text == null) return SUCCESS;
        for (PaymentStatus ps : PaymentStatus.values()) {
            if (ps.name().equalsIgnoreCase(text.trim()) || ps.displayName.equalsIgnoreCase(text.trim())) {
                return ps;
            }
        }
        return SUCCESS;
    }
}
