package com.smartparking.dao;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RecordStatus;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object Interface for Parking Records.
 */
public interface IParkingRecordDAO {
    Optional<ParkingRecord> findById(int recordId);
    Optional<ParkingRecord> findActiveByVehicleNumber(String vehicleNumber);
    List<ParkingRecord> findActiveRecords();
    List<ParkingRecord> findAll(int limit);
    List<ParkingRecord> search(String keyword, LocalDate fromDate, LocalDate toDate, RecordStatus status);
    
    // Core insert methods (with and without optional JDBC connection)
    ParkingRecord insert(ParkingRecord record);
    ParkingRecord insert(ParkingRecord record, Connection optionalConn);

    // Core completion methods (with and without optional JDBC connection)
    boolean completeParking(int recordId, LocalDateTime exitTime, int durationHours, double totalFee);
    boolean completeParking(int recordId, LocalDateTime exitTime, int durationHours, double totalFee, Connection optionalConn);

    int countVehiclesParkedToday();
    int countTotalCompleted();
    List<ParkingRecord> findRecentRecords(int limit);
}
