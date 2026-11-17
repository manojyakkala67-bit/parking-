package com.smartparking.dao;

import com.smartparking.dao.storage.DataStore;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RecordStatus;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Parking Records (Standalone In-Memory & Local Storage).
 */
public class ParkingRecordDAOImpl implements IParkingRecordDAO {

    private final DataStore store = DataStore.getInstance();

    @Override
    public Optional<ParkingRecord> findById(int recordId) {
        return store.findRecordById(recordId);
    }

    @Override
    public Optional<ParkingRecord> findActiveByVehicleNumber(String vehicleNumber) {
        return store.findActiveRecordByVehicleNumber(vehicleNumber);
    }

    @Override
    public List<ParkingRecord> findActiveRecords() {
        return store.findActiveRecords();
    }

    @Override
    public List<ParkingRecord> findAll(int limit) {
        return store.findAllRecords(limit);
    }

    @Override
    public List<ParkingRecord> search(String keyword, LocalDate fromDate, LocalDate toDate, RecordStatus status) {
        return store.searchRecords(keyword, fromDate, toDate, status);
    }

    @Override
    public ParkingRecord insert(ParkingRecord record) {
        return store.insertRecord(record);
    }

    @Override
    public ParkingRecord insert(ParkingRecord record, Connection optionalConn) {
        return store.insertRecord(record);
    }

    @Override
    public boolean completeParking(int recordId, LocalDateTime exitTime, int durationHours, double totalFee) {
        return store.completeParkingRecord(recordId, exitTime, durationHours, totalFee);
    }

    @Override
    public boolean completeParking(int recordId, LocalDateTime exitTime, int durationHours, double totalFee, Connection optionalConn) {
        return store.completeParkingRecord(recordId, exitTime, durationHours, totalFee);
    }

    @Override
    public int countVehiclesParkedToday() {
        return store.countVehiclesParkedToday();
    }

    @Override
    public int countTotalCompleted() {
        return store.countTotalCompleted();
    }

    @Override
    public List<ParkingRecord> findRecentRecords(int limit) {
        return store.findAllRecords(limit);
    }
}
