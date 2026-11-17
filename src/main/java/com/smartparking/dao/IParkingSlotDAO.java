package com.smartparking.dao;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.VehicleType;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object Interface for Parking Slots.
 */
public interface IParkingSlotDAO {
    Optional<ParkingSlot> findById(int slotId);
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
    List<ParkingSlot> findAll();
    List<ParkingSlot> findByFloor(int floorNumber);
    List<ParkingSlot> findByStatus(SlotStatus status);
    List<ParkingSlot> findAvailableSlots(VehicleType vehicleType);
    Optional<ParkingSlot> findNearestAvailableSlot(VehicleType vehicleType);
    
    boolean updateStatus(int slotId, SlotStatus status, Integer vehicleId);
    boolean updateStatus(int slotId, SlotStatus status, Integer vehicleId, Connection optionalConn);
    
    boolean insert(ParkingSlot slot);
    boolean update(ParkingSlot slot);
    boolean delete(int slotId);
    int countTotalSlots();
    int countSlotsByStatus(SlotStatus status);
}
