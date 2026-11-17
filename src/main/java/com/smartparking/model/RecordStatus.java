package com.smartparking.model;

/**
 * Enum representing status of a parking record transaction.
 */
public enum RecordStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    RecordStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RecordStatus fromString(String text) {
        if (text == null) return ACTIVE;
        for (RecordStatus rs : RecordStatus.values()) {
            if (rs.name().equalsIgnoreCase(text.trim()) || rs.displayName.equalsIgnoreCase(text.trim())) {
                return rs;
            }
        }
        return ACTIVE;
    }
}
