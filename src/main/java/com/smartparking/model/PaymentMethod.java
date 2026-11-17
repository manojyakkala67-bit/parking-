package com.smartparking.model;

/**
 * Enum representing accepted payment methods.
 */
public enum PaymentMethod {
    CASH("Cash"),
    CARD("Credit/Debit Card"),
    UPI("UPI / QR Code");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String text) {
        if (text == null) return CASH;
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(text.trim()) || pm.displayName.equalsIgnoreCase(text.trim())) {
                return pm;
            }
        }
        return CASH;
    }
}
