package com.smartparking.dao;

import com.smartparking.dao.storage.DataStore;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.VehicleType;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Parking Slots (Standalone In-Memory & Local Storage).
 */
public class ParkingSlotDAOImpl implements IParkingSlotDAO {

    private final DataStore store = DataStore.getInstance();

    @Override
    public Optional<ParkingSlot> findById(int slotId) {
        return store.findSlotById(slotId);
    }

    @Override
    public Optional<ParkingSlot> findBySlotNumber(String slotNumber) {
        return store.findSlotByNumber(slotNumber);
    }

    @Override
    public List<ParkingSlot> findAll() {
        return store.findAllSlots();
    }

    @Override
    public List<ParkingSlot> findByFloor(int floorNumber) {
        return store.findSlotsByFloor(floorNumber);
    }

    @Override
    public List<ParkingSlot> findByStatus(SlotStatus status) {
        return store.findSlotsByStatus(status);
    }

    @Override
    public List<ParkingSlot> findAvailableSlots(VehicleType vehicleType) {
        return store.findAvailableSlots(vehicleType);
    }

    @Override
    public Optional<ParkingSlot> findNearestAvailableSlot(VehicleType vehicleType) {
        return store.findNearestAvailableSlot(vehicleType);
    }

    @Override
    public boolean updateStatus(int slotId, SlotStatus status, Integer vehicleId) {
        return store.updateSlotStatus(slotId, status, vehicleId);
    }

    @Override
    public boolean updateStatus(int slotId, SlotStatus status, Integer vehicleId, Connection optionalConn) {
        return store.updateSlotStatus(slotId, status, vehicleId);
    }

    @Override
    public boolean insert(ParkingSlot slot) {
        return store.insertSlot(slot);
    }

    @Override
    public boolean update(ParkingSlot slot) {
        return store.updateSlot(slot);
    }

    @Override
    public boolean delete(int slotId) {
        return store.deleteSlot(slotId);
    }

    @Override
    public int countTotalSlots() {
        return store.countTotalSlots();
    }

    @Override
    public int countSlotsByStatus(SlotStatus status) {
        return store.countSlotsByStatus(status);
    }
}
