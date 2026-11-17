package com.smartparking.model;

/**
 * Enum representing supported vehicle types and their hourly rates.
 */
public enum VehicleType {
    BIKE("Bike", 40.0),
    CAR("Car", 100.0),
    TRUCK("Truck", 150.0);

    private final String displayName;
    private final double hourlyRate;

    VehicleType(String displayName, double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public static VehicleType fromString(String text) {
        if (text == null) return null;
        for (VehicleType vt : VehicleType.values()) {
            if (vt.name().equalsIgnoreCase(text.trim()) || vt.displayName.equalsIgnoreCase(text.trim())) {
                return vt;
            }
        }
        throw new IllegalArgumentException("Unknown VehicleType: " + text);
    }

    @Override
    public String toString() {
        return displayName + " (₹" + (int)hourlyRate + "/hr)";
    }
}
