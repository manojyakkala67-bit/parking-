package com.smartparking.service;

import com.smartparking.dao.IParkingSlotDAO;
import com.smartparking.dao.ParkingSlotDAOImpl;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.SlotType;
import com.smartparking.model.VehicleType;
import com.smartparking.util.ParkingException;
import com.smartparking.util.ValidationException;
import com.smartparking.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of IParkingSlotService.
 */
public class ParkingSlotServiceImpl implements IParkingSlotService {

    private final IParkingSlotDAO slotDAO;

    public ParkingSlotServiceImpl() {
        this.slotDAO = new ParkingSlotDAOImpl();
    }

    public ParkingSlotServiceImpl(IParkingSlotDAO slotDAO) {
        this.slotDAO = slotDAO;
    }

    @Override
    public List<ParkingSlot> getAllSlots() {
        return slotDAO.findAll();
    }

    @Override
    public List<ParkingSlot> getSlotsByFloor(int floor) {
        return slotDAO.findByFloor(floor);
    }

    @Override
    public List<ParkingSlot> getSlotsByStatus(SlotStatus status) {
        return slotDAO.findByStatus(status);
    }

    @Override
    public Optional<ParkingSlot> getSlotByNumber(String slotNumber) {
        if (slotNumber == null || slotNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return slotDAO.findBySlotNumber(slotNumber.trim().toUpperCase());
    }

    @Override
    public Optional<ParkingSlot> getSlotById(int slotId) {
        return slotDAO.findById(slotId);
    }

    @Override
    public List<ParkingSlot> getAvailableSlots(VehicleType vehicleType) {
        return slotDAO.findAvailableSlots(vehicleType);
    }

    @Override
    public Optional<ParkingSlot> getNearestAvailableSlot(VehicleType vehicleType) {
        return slotDAO.findNearestAvailableSlot(vehicleType);
    }

    @Override
    public ParkingSlot addSlot(String slotNumber, int floor, SlotType type) {
        ValidationUtil.validateSlotNumber(slotNumber);
        if (floor <= 0) {
            throw new ValidationException("Floor number must be at least 1.");
        }
        if (type == null) {
            type = SlotType.GENERAL;
        }

        String norm = slotNumber.trim().toUpperCase();
        if (slotDAO.findBySlotNumber(norm).isPresent()) {
            throw new ValidationException("A slot with number " + norm + " already exists.");
        }

        ParkingSlot slot = new ParkingSlot(norm, floor, type);
        boolean ok = slotDAO.insert(slot);
        if (!ok) {
            throw new ParkingException("Failed to add parking slot.");
        }
        return slot;
    }

    @Override
    public boolean updateSlot(int slotId, String slotNumber, int floor, SlotType type, SlotStatus status) {
        ValidationUtil.validateSlotNumber(slotNumber);
        if (floor <= 0) {
            throw new ValidationException("Floor number must be at least 1.");
        }

        Optional<ParkingSlot> opt = slotDAO.findById(slotId);
        if (opt.isEmpty()) {
            throw new ParkingException("Slot not found with ID: " + slotId);
        }

        ParkingSlot slot = opt.get();
        if (slot.isOccupied() && status != SlotStatus.OCCUPIED) {
            throw new ParkingException("Cannot change status of an occupied slot directly. Use Vehicle Exit.");
        }

        slot.setSlotNumber(slotNumber.trim().toUpperCase());
        slot.setFloorNumber(floor);
        slot.setSlotType(type);
        slot.setStatus(status);

        return slotDAO.update(slot);
    }

    @Override
    public boolean deleteSlot(int slotId) {
        Optional<ParkingSlot> opt = slotDAO.findById(slotId);
        if (opt.isEmpty()) {
            throw new ParkingException("Slot not found with ID: " + slotId);
        }
        if (opt.get().isOccupied()) {
            throw new ParkingException("Cannot delete an occupied parking slot.");
        }
        return slotDAO.delete(slotId);
    }

    @Override
    public boolean toggleMaintenance(int slotId) {
        Optional<ParkingSlot> opt = slotDAO.findById(slotId);
        if (opt.isEmpty()) {
            throw new ParkingException("Slot not found with ID: " + slotId);
        }

        ParkingSlot slot = opt.get();
        if (slot.isOccupied()) {
            throw new ParkingException("Cannot set an occupied slot to maintenance.");
        }

        SlotStatus newStatus = slot.isUnderMaintenance() ? SlotStatus.AVAILABLE : SlotStatus.MAINTENANCE;
        return slotDAO.updateStatus(slotId, newStatus, null, null);
    }
}
