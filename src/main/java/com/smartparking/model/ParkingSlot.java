package com.smartparking.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing a designated Parking Slot.
 */
public class ParkingSlot {
    private int slotId;
    private String slotNumber;
    private int floorNumber;
    private SlotType slotType;
    private SlotStatus status;
    private Integer currentVehicleId;
    private String currentVehicleNumber; // Transient / join field for quick UI display
    private LocalDateTime createdAt;

    public ParkingSlot() {
        this.status = SlotStatus.AVAILABLE;
        this.floorNumber = 1;
        this.slotType = SlotType.GENERAL;
    }

    public ParkingSlot(String slotNumber, int floorNumber, SlotType slotType) {
        setSlotNumber(slotNumber);
        this.floorNumber = floorNumber;
        this.slotType = Objects.requireNonNull(slotType, "Slot type cannot be null");
        this.status = SlotStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    public ParkingSlot(int slotId, String slotNumber, int floorNumber, 
                       SlotType slotType, SlotStatus status, Integer currentVehicleId, LocalDateTime createdAt) {
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.floorNumber = floorNumber;
        this.slotType = slotType;
        this.status = status;
        this.currentVehicleId = currentVehicleId;
        this.createdAt = createdAt;
    }

    public boolean isAvailable() {
        return this.status == SlotStatus.AVAILABLE;
    }

    public boolean isOccupied() {
        return this.status == SlotStatus.OCCUPIED;
    }

    public boolean isUnderMaintenance() {
        return this.status == SlotStatus.MAINTENANCE;
    }

    // Getters and Setters

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        if (slotNumber == null || slotNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot number cannot be empty");
        }
        this.slotNumber = slotNumber.trim().toUpperCase();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public Integer getCurrentVehicleId() {
        return currentVehicleId;
    }

    public void setCurrentVehicleId(Integer currentVehicleId) {
        this.currentVehicleId = currentVehicleId;
    }

    public String getCurrentVehicleNumber() {
        return currentVehicleNumber;
    }

    public void setCurrentVehicleNumber(String currentVehicleNumber) {
        this.currentVehicleNumber = currentVehicleNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingSlot that)) return false;
        return Objects.equals(slotNumber, that.slotNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotNumber);
    }

    @Override
    public String toString() {
        return String.format("Slot %s (Floor %d, %s) [%s]", slotNumber, floorNumber, slotType, status);
    }
}
