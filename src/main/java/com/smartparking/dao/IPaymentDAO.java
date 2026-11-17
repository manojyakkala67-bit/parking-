package com.smartparking.dao;

import com.smartparking.model.Payment;
import com.smartparking.model.RevenueReport;
import com.smartparking.model.VehicleType;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object Interface for Payments and Revenue Aggregations.
 */
public interface IPaymentDAO {
    Payment insert(Payment payment);
    Payment insert(Payment payment, Connection optionalConn);
    Optional<Payment> findByRecordId(int recordId);
    Optional<Payment> findById(int paymentId);
    double calculateTotalRevenue();
    double calculateTodayRevenue();
    double calculateMonthRevenue();
    Map<VehicleType, Double> calculateRevenueByVehicleType();
    Map<VehicleType, Integer> calculateVehicleCountByType();
    RevenueReport generateReport(LocalDate startDate, LocalDate endDate);
}
