package com.smartparking.model;

/**
 * Enum representing categories of parking slots.
 */
public enum SlotType {
    BIKE("Bike Slot"),
    CAR("Car Slot"),
    TRUCK("Truck Slot"),
    GENERAL("General Slot");

    private final String displayName;

    SlotType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canAccommodate(VehicleType vehicleType) {
        if (this == GENERAL) return true;
        return this.name().equals(vehicleType.name());
    }

    public static SlotType fromString(String text) {
        if (text == null) return GENERAL;
        for (SlotType st : SlotType.values()) {
            if (st.name().equalsIgnoreCase(text.trim()) || st.displayName.equalsIgnoreCase(text.trim())) {
                return st;
            }
        }
        return GENERAL;
    }
}
