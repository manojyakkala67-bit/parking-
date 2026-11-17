package com.smartparking.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class encapsulating a financial revenue report over a date range.
 */
public class RevenueReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalRevenue;
    private int totalTransactions;
    private double bikeRevenue;
    private int bikeCount;
    private double carRevenue;
    private int carCount;
    private double truckRevenue;
    private int truckCount;
    private List<ParkingRecord> records = new ArrayList<>();

    public RevenueReport() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public double getBikeRevenue() {
        return bikeRevenue;
    }

    public void setBikeRevenue(double bikeRevenue) {
        this.bikeRevenue = bikeRevenue;
    }

    public int getBikeCount() {
        return bikeCount;
    }

    public void setBikeCount(int bikeCount) {
        this.bikeCount = bikeCount;
    }

    public double getCarRevenue() {
        return carRevenue;
    }

    public void setCarRevenue(double carRevenue) {
        this.carRevenue = carRevenue;
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public double getTruckRevenue() {
        return truckRevenue;
    }

    public void setTruckRevenue(double truckRevenue) {
        this.truckRevenue = truckRevenue;
    }

    public int getTruckCount() {
        return truckCount;
    }

    public void setTruckCount(int truckCount) {
        this.truckCount = truckCount;
    }

    public List<ParkingRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ParkingRecord> records) {
        this.records = records;
    }
}
