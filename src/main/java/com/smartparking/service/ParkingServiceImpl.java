package com.smartparking.service;

import com.smartparking.dao.IParkingRecordDAO;
import com.smartparking.dao.IParkingSlotDAO;
import com.smartparking.dao.IPaymentDAO;
import com.smartparking.dao.IVehicleDAO;
import com.smartparking.dao.ParkingRecordDAOImpl;
import com.smartparking.dao.ParkingSlotDAOImpl;
import com.smartparking.dao.PaymentDAOImpl;
import com.smartparking.dao.VehicleDAOImpl;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.Payment;
import com.smartparking.model.PaymentMethod;
import com.smartparking.model.PaymentStatus;
import com.smartparking.model.RecordStatus;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleFactory;
import com.smartparking.model.VehicleType;
import com.smartparking.util.DuplicateVehicleException;
import com.smartparking.util.ParkingException;
import com.smartparking.util.SlotUnavailableException;
import com.smartparking.util.ValidationException;
import com.smartparking.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of IParkingService managing entry, exit, and billing with transactional integrity.
 */
public class ParkingServiceImpl implements IParkingService {

    private final IVehicleDAO vehicleDAO;
    private final IParkingSlotDAO slotDAO;
    private final IParkingRecordDAO recordDAO;
    private final IPaymentDAO paymentDAO;

    public ParkingServiceImpl() {
        this.vehicleDAO = new VehicleDAOImpl();
        this.slotDAO = new ParkingSlotDAOImpl();
        this.recordDAO = new ParkingRecordDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    public ParkingServiceImpl(IVehicleDAO vehicleDAO, IParkingSlotDAO slotDAO, 
                              IParkingRecordDAO recordDAO, IPaymentDAO paymentDAO) {
        this.vehicleDAO = vehicleDAO;
        this.slotDAO = slotDAO;
        this.recordDAO = recordDAO;
        this.paymentDAO = paymentDAO;
    }

    @Override
    public synchronized ParkingRecord processVehicleEntry(String regNo, VehicleType type, String ownerName, String phone, Integer chosenSlotId) {
        ValidationUtil.validateRegistrationNumber(regNo);
        ValidationUtil.validateOwnerName(ownerName);
        ValidationUtil.validatePhoneNumber(phone);
        if (type == null) {
            throw new ValidationException("Vehicle type must be selected.");
        }

        String normReg = regNo.trim().toUpperCase();

        // 1. Prevent duplicate active parking for the same vehicle
        Optional<ParkingRecord> activeRecOpt = recordDAO.findActiveByVehicleNumber(normReg);
        if (activeRecOpt.isPresent()) {
            ParkingRecord active = activeRecOpt.get();
            throw new DuplicateVehicleException(
                String.format("Vehicle %s is already actively parked in slot %s (entered: %s).",
                        normReg, active.getSlotNumber(), active.getEntryTime())
            );
        }

        // 2. Ensure Vehicle is in the vehicles registry
        Optional<Vehicle> regVehicleOpt = vehicleDAO.findByRegistrationNumber(normReg);
        Vehicle vehicle;
        if (regVehicleOpt.isEmpty()) {
            vehicle = VehicleFactory.createVehicle(type, normReg, ownerName.trim(), phone.trim());
            vehicle = vehicleDAO.insert(vehicle);
        } else {
            vehicle = regVehicleOpt.get();
            vehicle.setVehicleType(type);
            vehicle.setOwnerName(ownerName.trim());
            vehicle.setPhoneNumber(phone.trim());
            vehicleDAO.update(vehicle);
        }

        // 3. Find target parking slot
        ParkingSlot targetSlot;
        if (chosenSlotId != null && chosenSlotId > 0) {
            Optional<ParkingSlot> slotOpt = slotDAO.findById(chosenSlotId);
            if (slotOpt.isEmpty()) {
                throw new ParkingException("Selected parking slot does not exist.");
            }
            targetSlot = slotOpt.get();
            if (!targetSlot.isAvailable()) {
                throw new SlotUnavailableException("Selected slot " + targetSlot.getSlotNumber() + " is no longer available.");
            }
            if (!targetSlot.getSlotType().canAccommodate(type)) {
                throw new ValidationException("Selected slot " + targetSlot.getSlotNumber() + " cannot accommodate " + type.getDisplayName() + ".");
            }
        } else {
            Optional<ParkingSlot> nearestOpt = slotDAO.findNearestAvailableSlot(type);
            if (nearestOpt.isEmpty()) {
                throw new SlotUnavailableException("No parking slot currently available for " + type.getDisplayName() + ".");
            }
            targetSlot = nearestOpt.get();
        }

        // 4. Create parking record & mark slot as occupied
        ParkingRecord record = new ParkingRecord();
        record.setVehicleId(vehicle.getVehicleId());
        record.setVehicleNumber(normReg);
        record.setVehicleType(type);
        record.setOwnerName(ownerName.trim());
        record.setPhoneNumber(phone.trim());
        record.setSlotId(targetSlot.getSlotId());
        record.setSlotNumber(targetSlot.getSlotNumber());
        record.setEntryTime(LocalDateTime.now());
        record.setStatus(RecordStatus.ACTIVE);

        record = recordDAO.insert(record);
        slotDAO.updateStatus(targetSlot.getSlotId(), SlotStatus.OCCUPIED, vehicle.getVehicleId());

        return record;
    }

    @Override
    public Optional<ParkingRecord> getActiveParking(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return recordDAO.findActiveByVehicleNumber(regNo.trim().toUpperCase());
    }

    @Override
    public List<ParkingRecord> getActiveParkings() {
        return recordDAO.findActiveRecords();
    }

    @Override
    public double calculateEstimatedFee(ParkingRecord record, LocalDateTime exitTime) {
        if (record == null) return 0.0;
        long hours = record.calculateBillableHours(exitTime != null ? exitTime : LocalDateTime.now());
        Vehicle vehicle = VehicleFactory.createVehicle(
                record.getVehicleType(), 
                record.getVehicleNumber(), 
                record.getOwnerName(), 
                record.getPhoneNumber()
        );
        return vehicle.calculateFee(hours);
    }

    @Override
    public synchronized Payment processVehicleExit(int recordId, PaymentMethod method, double amountReceived) {
        Optional<ParkingRecord> optRecord = recordDAO.findById(recordId);
        if (optRecord.isEmpty()) {
            throw new ParkingException("Parking record not found with ID: " + recordId);
        }

        ParkingRecord record = optRecord.get();
        if (record.getStatus() != RecordStatus.ACTIVE) {
            throw new ParkingException("This parking transaction is already completed or cancelled.");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        long hours = record.calculateBillableHours(exitTime);

        // Polymorphic fee calculation
        Vehicle vehicle = VehicleFactory.createVehicle(
                record.getVehicleType(), 
                record.getVehicleNumber(), 
                record.getOwnerName(), 
                record.getPhoneNumber()
        );
        double totalFee = vehicle.calculateFee(hours);

        if (amountReceived < totalFee) {
            throw new ValidationException(String.format("Payment amount insufficient. Required: ₹%.2f, Received: ₹%.2f", totalFee, amountReceived));
        }

        // Generate Transaction Reference
        String txnRef = String.format("TXN-%d-%05d", System.currentTimeMillis() % 100000000L, recordId);

        Payment payment = new Payment();
        payment.setRecordId(recordId);
        payment.setAmount(totalFee);
        payment.setPaymentMethod(method != null ? method : PaymentMethod.CASH);
        payment.setPaymentTime(exitTime);
        payment.setTransactionReference(txnRef);
        payment.setStatus(PaymentStatus.SUCCESS);

        // 1. Update parking record to COMPLETED
        recordDAO.completeParking(recordId, exitTime, (int) hours, totalFee);

        // 2. Insert payment
        paymentDAO.insert(payment);

        // 3. Release slot to AVAILABLE
        slotDAO.updateStatus(record.getSlotId(), SlotStatus.AVAILABLE, null);

        // Update in-memory record for caller
        record.setExitTime(exitTime);
        record.setDurationHours((int) hours);
        record.setTotalFee(totalFee);
        record.setStatus(RecordStatus.COMPLETED);

        return payment;
    }

    @Override
    public Optional<ParkingRecord> getParkingRecordById(int recordId) {
        return recordDAO.findById(recordId);
    }
}
