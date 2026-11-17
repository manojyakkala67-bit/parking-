package com.smartparking.service;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.Payment;
import com.smartparking.model.PaymentMethod;
import com.smartparking.model.VehicleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for vehicle entry, exit, billing calculations, and transactions.
 */
public interface IParkingService {
    ParkingRecord processVehicleEntry(String regNo, VehicleType type, String ownerName, String phone, Integer chosenSlotId);
    Optional<ParkingRecord> getActiveParking(String regNo);
    List<ParkingRecord> getActiveParkings();
    double calculateEstimatedFee(ParkingRecord record, LocalDateTime exitTime);
    Payment processVehicleExit(int recordId, PaymentMethod method, double amountReceived);
    Optional<ParkingRecord> getParkingRecordById(int recordId);
}
