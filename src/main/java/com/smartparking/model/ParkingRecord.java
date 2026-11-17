package com.smartparking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Model class representing a vehicle parking transaction record.
 */
public class ParkingRecord {
    private int recordId;
    private Integer vehicleId;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String ownerName;
    private String phoneNumber;
    private int slotId;
    private String slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Integer durationHours;
    private Double totalFee;
    private RecordStatus status;
    private LocalDateTime createdAt;

    public ParkingRecord() {
        this.status = RecordStatus.ACTIVE;
        this.entryTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public ParkingRecord(String vehicleNumber, VehicleType vehicleType, String ownerName, 
                         String phoneNumber, int slotId, String slotNumber) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.entryTime = LocalDateTime.now();
        this.status = RecordStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public ParkingRecord(int recordId, Integer vehicleId, String vehicleNumber, VehicleType vehicleType,
                         String ownerName, String phoneNumber, int slotId, String slotNumber,
                         LocalDateTime entryTime, LocalDateTime exitTime, Integer durationHours,
                         Double totalFee, RecordStatus status, LocalDateTime createdAt) {
        this.recordId = recordId;
        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.durationHours = durationHours;
        this.totalFee = totalFee;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Calculates billable started hours up to a target exit time (or current time if null).
     * Minimum 1 hour charge. Any fraction of an hour counts as an additional hour.
     */
    public long calculateBillableHours(LocalDateTime untilTime) {
        if (entryTime == null) return 1;
        LocalDateTime end = (untilTime != null) ? untilTime : LocalDateTime.now();
        Duration duration = Duration.between(entryTime, end);
        long minutes = Math.max(0, duration.toMinutes());
        if (minutes == 0) return 1;
        // Ceiling division: minutes / 60 rounded up
        long hours = (minutes + 59) / 60;
        return Math.max(1, hours);
    }

    public String getFormattedDuration() {
        if (entryTime == null) return "0 mins";
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        Duration duration = Duration.between(entryTime, end);
        long minutes = Math.max(0, duration.toMinutes());
        long hrs = minutes / 60;
        long mins = minutes % 60;
        if (hrs == 0) {
            return mins + " min" + (mins == 1 ? "" : "s");
        }
        return hrs + " hr" + (hrs == 1 ? "" : "s") + " " + mins + " min" + (mins == 1 ? "" : "s");
    }

    public boolean isActive() {
        return this.status == RecordStatus.ACTIVE;
    }

    // Getters and Setters

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : null;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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
        this.slotNumber = slotNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
