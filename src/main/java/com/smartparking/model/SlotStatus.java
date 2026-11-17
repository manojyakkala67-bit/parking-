package com.smartparking.model;

/**
 * Enum representing status of a parking slot.
 */
public enum SlotStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    SlotStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SlotStatus fromString(String text) {
        if (text == null) return AVAILABLE;
        for (SlotStatus ss : SlotStatus.values()) {
            if (ss.name().equalsIgnoreCase(text.trim()) || ss.displayName.equalsIgnoreCase(text.trim())) {
                return ss;
            }
        }
        return AVAILABLE;
    }
}
