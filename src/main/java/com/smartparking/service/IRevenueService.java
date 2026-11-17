package com.smartparking.service;

import com.smartparking.model.DashboardStats;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RecordStatus;
import com.smartparking.model.RevenueReport;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for revenue analytics, dashboard statistics, and audit history.
 */
public interface IRevenueService {
    DashboardStats getDashboardStats();
    RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate);
    List<ParkingRecord> searchHistory(String keyword, LocalDate from, LocalDate to, RecordStatus status);
    List<ParkingRecord> getRecentTransactions(int limit);
}
