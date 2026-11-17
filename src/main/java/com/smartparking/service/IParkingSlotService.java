package com.smartparking.service;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.SlotType;
import com.smartparking.model.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Parking Slot Management.
 */
public interface IParkingSlotService {
    List<ParkingSlot> getAllSlots();
    List<ParkingSlot> getSlotsByFloor(int floor);
    List<ParkingSlot> getSlotsByStatus(SlotStatus status);
    Optional<ParkingSlot> getSlotByNumber(String slotNumber);
    Optional<ParkingSlot> getSlotById(int slotId);
    List<ParkingSlot> getAvailableSlots(VehicleType vehicleType);
    Optional<ParkingSlot> getNearestAvailableSlot(VehicleType vehicleType);
    ParkingSlot addSlot(String slotNumber, int floor, SlotType type);
    boolean updateSlot(int slotId, String slotNumber, int floor, SlotType type, SlotStatus status);
    boolean deleteSlot(int slotId);
    boolean toggleMaintenance(int slotId);
}
