package com.smartparking.dao;

import com.smartparking.dao.storage.DataStore;
import com.smartparking.model.Payment;
import com.smartparking.model.RevenueReport;
import com.smartparking.model.VehicleType;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for Payments and Revenue Aggregations (Standalone In-Memory & Local Storage).
 */
public class PaymentDAOImpl implements IPaymentDAO {

    private final DataStore store = DataStore.getInstance();

    @Override
    public Payment insert(Payment payment) {
        return store.insertPayment(payment);
    }

    @Override
    public Payment insert(Payment payment, Connection optionalConn) {
        return store.insertPayment(payment);
    }

    @Override
    public Optional<Payment> findByRecordId(int recordId) {
        return store.findPaymentByRecordId(recordId);
    }

    @Override
    public Optional<Payment> findById(int paymentId) {
        return Optional.empty();
    }

    @Override
    public double calculateTotalRevenue() {
        return store.calculateTotalRevenue();
    }

    @Override
    public double calculateTodayRevenue() {
        return store.calculateTodayRevenue();
    }

    @Override
    public double calculateMonthRevenue() {
        return store.calculateMonthRevenue();
    }

    @Override
    public Map<VehicleType, Double> calculateRevenueByVehicleType() {
        return store.calculateRevenueByVehicleType();
    }

    @Override
    public Map<VehicleType, Integer> calculateVehicleCountByType() {
        return store.calculateVehicleCountByType();
    }

    @Override
    public RevenueReport generateReport(LocalDate startDate, LocalDate endDate) {
        return store.generateRevenueReport(startDate, endDate);
    }
}
