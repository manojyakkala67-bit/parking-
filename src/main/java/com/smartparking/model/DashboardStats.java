package com.smartparking.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Model class holding aggregated statistics for the Dashboard.
 */
public class DashboardStats {
    private int totalSlots;
    private int availableSlots;
    private int occupiedSlots;
    private int maintenanceSlots;
    private int totalVehiclesCurrentlyParked;
    private double totalRevenue;
    private double todayRevenue;
    private double monthRevenue;
    private int vehiclesParkedToday;
    private int totalCompletedTransactions;
    private Map<VehicleType, Integer> vehicleCountByType = new EnumMap<>(VehicleType.class);
    private Map<VehicleType, Double> revenueByType = new EnumMap<>(VehicleType.class);

    public DashboardStats() {
        for (VehicleType vt : VehicleType.values()) {
            vehicleCountByType.put(vt, 0);
            revenueByType.put(vt, 0.0);
        }
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(int occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public int getMaintenanceSlots() {
        return maintenanceSlots;
    }

    public void setMaintenanceSlots(int maintenanceSlots) {
        this.maintenanceSlots = maintenanceSlots;
    }

    public int getTotalVehiclesCurrentlyParked() {
        return totalVehiclesCurrentlyParked;
    }

    public void setTotalVehiclesCurrentlyParked(int totalVehiclesCurrentlyParked) {
        this.totalVehiclesCurrentlyParked = totalVehiclesCurrentlyParked;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public double getMonthRevenue() {
        return monthRevenue;
    }

    public void setMonthRevenue(double monthRevenue) {
        this.monthRevenue = monthRevenue;
    }

    public int getVehiclesParkedToday() {
        return vehiclesParkedToday;
    }

    public void setVehiclesParkedToday(int vehiclesParkedToday) {
        this.vehiclesParkedToday = vehiclesParkedToday;
    }

    public int getTotalCompletedTransactions() {
        return totalCompletedTransactions;
    }

    public void setTotalCompletedTransactions(int totalCompletedTransactions) {
        this.totalCompletedTransactions = totalCompletedTransactions;
    }

    public Map<VehicleType, Integer> getVehicleCountByType() {
        return vehicleCountByType;
    }

    public void setVehicleCountByType(Map<VehicleType, Integer> vehicleCountByType) {
        this.vehicleCountByType = vehicleCountByType;
    }

    public Map<VehicleType, Double> getRevenueByType() {
        return revenueByType;
    }

    public void setRevenueByType(Map<VehicleType, Double> revenueByType) {
        this.revenueByType = revenueByType;
    }

    public double getOccupancyPercentage() {
        if (totalSlots == 0) return 0.0;
        return ((double) occupiedSlots / totalSlots) * 100.0;
    }
}
