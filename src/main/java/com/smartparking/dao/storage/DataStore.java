package com.smartparking.dao.storage;

import com.smartparking.model.Admin;
import com.smartparking.model.Bike;
import com.smartparking.model.Car;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.Payment;
import com.smartparking.model.PaymentMethod;
import com.smartparking.model.PaymentStatus;
import com.smartparking.model.RecordStatus;
import com.smartparking.model.RevenueReport;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.SlotType;
import com.smartparking.model.Truck;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleFactory;
import com.smartparking.model.VehicleType;
import com.smartparking.util.PasswordUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * High-performance, thread-safe, in-memory embedded Data Store with automatic local persistence.
 * Completely eliminates the need for an external MySQL server installation.
 */
public final class DataStore {

    private static final String DATA_FILE = "data/parking_system_data.dat";
    private static DataStore instance;

    // Collections
    private final Map<Integer, Admin> admins = new LinkedHashMap<>();
    private final Map<Integer, Vehicle> vehicles = new LinkedHashMap<>();
    private final Map<Integer, ParkingSlot> slots = new LinkedHashMap<>();
    private final List<ParkingRecord> records = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();

    // ID Generators
    private final AtomicInteger adminIdGen = new AtomicInteger(1);
    private final AtomicInteger vehicleIdGen = new AtomicInteger(1);
    private final AtomicInteger slotIdGen = new AtomicInteger(1);
    private final AtomicInteger recordIdGen = new AtomicInteger(1);
    private final AtomicInteger paymentIdGen = new AtomicInteger(1);

    private DataStore() {
        seedInitialData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Seeds initial admin account, 45 parking bays across 2 floors, sample vehicles, and records.
     */
    private void seedInitialData() {
        // 1. Seed Default Admin: admin / admin123
        String salt = "e3b0c44298fc1c14";
        String hash = PasswordUtil.hashPassword("admin123", salt);
        Admin admin = new Admin(
                adminIdGen.getAndIncrement(),
                "admin",
                hash,
                salt,
                "System Administrator",
                "admin@smartparking.com",
                "SUPER_ADMIN",
                LocalDateTime.now(),
                null
        );
        admins.put(admin.getAdminId(), admin);

        // 2. Seed Sample Vehicles
        Vehicle v1 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.BIKE, "KA-01-AB-1234", "Rahul Sharma", "9876543210", LocalDateTime.now().minusDays(3), LocalDateTime.now());
        Vehicle v2 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.CAR, "DL-03-XY-9876", "Priya Verma", "9812345678", LocalDateTime.now().minusDays(2), LocalDateTime.now());
        Vehicle v3 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.CAR, "MH-12-CD-5544", "Amit Patel", "9823456789", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Vehicle v4 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.TRUCK, "HR-26-EE-4321", "Vikram Singh", "9834567890", LocalDateTime.now().minusDays(4), LocalDateTime.now());
        Vehicle v5 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.CAR, "TS-09-FA-7788", "Sneha Reddy", "9845678901", LocalDateTime.now().minusDays(2), LocalDateTime.now());
        Vehicle v6 = VehicleFactory.createVehicle(vehicleIdGen.getAndIncrement(), VehicleType.BIKE, "UP-32-ZZ-1122", "Mohit Gupta", "9856789012", LocalDateTime.now().minusDays(1), LocalDateTime.now());

        vehicles.put(v1.getVehicleId(), v1);
        vehicles.put(v2.getVehicleId(), v2);
        vehicles.put(v3.getVehicleId(), v3);
        vehicles.put(v4.getVehicleId(), v4);
        vehicles.put(v5.getVehicleId(), v5);
        vehicles.put(v6.getVehicleId(), v6);

        // 3. Seed Floor 1 Parking Slots:
        // Bike Slots (A-101 to A-110)
        for (int i = 1; i <= 10; i++) {
            String num = String.format("A-1%02d", i);
            int id = slotIdGen.getAndIncrement();
            slots.put(id, new ParkingSlot(id, num, 1, SlotType.BIKE, SlotStatus.AVAILABLE, null, LocalDateTime.now()));
        }

        // Car Slots (A-111 to A-125)
        for (int i = 11; i <= 25; i++) {
            String num = String.format("A-1%02d", i);
            int id = slotIdGen.getAndIncrement();
            slots.put(id, new ParkingSlot(id, num, 1, SlotType.CAR, SlotStatus.AVAILABLE, null, LocalDateTime.now()));
        }

        // Floor 2: Car Slots (B-201 to B-210)
        for (int i = 1; i <= 10; i++) {
            String num = String.format("B-2%02d", i);
            int id = slotIdGen.getAndIncrement();
            slots.put(id, new ParkingSlot(id, num, 2, SlotType.CAR, SlotStatus.AVAILABLE, null, LocalDateTime.now()));
        }

        // Floor 2: Truck Slots (B-211 to B-220)
        for (int i = 11; i <= 20; i++) {
            String num = String.format("B-2%02d", i);
            int id = slotIdGen.getAndIncrement();
            slots.put(id, new ParkingSlot(id, num, 2, SlotType.TRUCK, SlotStatus.AVAILABLE, null, LocalDateTime.now()));
        }

        // 4. Seed Historical Completed Parking Transactions & Real-time Revenues
        ParkingRecord r1 = new ParkingRecord(
                recordIdGen.getAndIncrement(), v1.getVehicleId(), v1.getRegistrationNumber(),
                v1.getVehicleType(), v1.getOwnerName(), v1.getPhoneNumber(),
                1, "A-101",
                LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(3),
                2, 80.0, RecordStatus.COMPLETED, LocalDateTime.now().minusHours(5)
        );
        records.add(r1);
        payments.add(new Payment(
                paymentIdGen.getAndIncrement(), r1.getRecordId(), 80.0,
                PaymentMethod.UPI, LocalDateTime.now().minusHours(3), "TXN-SMP-1001-UPI", PaymentStatus.SUCCESS, LocalDateTime.now().minusHours(3)
        ));

        ParkingRecord r2 = new ParkingRecord(
                recordIdGen.getAndIncrement(), v2.getVehicleId(), v2.getRegistrationNumber(),
                v2.getVehicleType(), v2.getOwnerName(), v2.getPhoneNumber(),
                11, "A-111",
                LocalDateTime.now().minusHours(4), LocalDateTime.now().minusHours(1),
                3, 300.0, RecordStatus.COMPLETED, LocalDateTime.now().minusHours(4)
        );
        records.add(r2);
        payments.add(new Payment(
                paymentIdGen.getAndIncrement(), r2.getRecordId(), 300.0,
                PaymentMethod.CASH, LocalDateTime.now().minusHours(1), "TXN-SMP-1002-CSH", PaymentStatus.SUCCESS, LocalDateTime.now().minusHours(1)
        ));

        ParkingRecord r3 = new ParkingRecord(
                recordIdGen.getAndIncrement(), v4.getVehicleId(), v4.getRegistrationNumber(),
                v4.getVehicleType(), v4.getOwnerName(), v4.getPhoneNumber(),
                36, "B-211",
                LocalDateTime.now().minusHours(8), LocalDateTime.now().minusHours(4),
                4, 600.0, RecordStatus.COMPLETED, LocalDateTime.now().minusHours(8)
        );
        records.add(r3);
        payments.add(new Payment(
                paymentIdGen.getAndIncrement(), r3.getRecordId(), 600.0,
                PaymentMethod.CARD, LocalDateTime.now().minusHours(4), "TXN-SMP-1003-CRD", PaymentStatus.SUCCESS, LocalDateTime.now().minusHours(4)
        ));
    }

    // =========================================================================
    // ADMIN OPERATIONS
    // =========================================================================

    public synchronized Optional<Admin> findAdminByUsername(String username) {
        if (username == null) return Optional.empty();
        for (Admin a : admins.values()) {
            if (a.getUsername().equalsIgnoreCase(username.trim())) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    public synchronized Optional<Admin> findAdminById(int adminId) {
        return Optional.ofNullable(admins.get(adminId));
    }

    public synchronized boolean updateAdminPassword(int adminId, String newHash, String newSalt) {
        Admin a = admins.get(adminId);
        if (a != null) {
            a.setPasswordHash(newHash);
            a.setSalt(newSalt);
            return true;
        }
        return false;
    }

    public synchronized boolean updateAdminLastLogin(int adminId) {
        Admin a = admins.get(adminId);
        if (a != null) {
            a.setLastLogin(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // =========================================================================
    // VEHICLE OPERATIONS
    // =========================================================================

    public synchronized Optional<Vehicle> findVehicleById(int id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    public synchronized Optional<Vehicle> findVehicleByRegNo(String regNo) {
        if (regNo == null) return Optional.empty();
        String target = regNo.trim().toUpperCase();
        for (Vehicle v : vehicles.values()) {
            if (v.getRegistrationNumber().equalsIgnoreCase(target)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public synchronized List<Vehicle> findAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    public synchronized List<Vehicle> searchVehicles(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllVehicles();
        }
        String term = keyword.trim().toLowerCase();
        List<Vehicle> matches = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            if (v.getRegistrationNumber().toLowerCase().contains(term) ||
                v.getOwnerName().toLowerCase().contains(term) ||
                v.getPhoneNumber().contains(term)) {
                matches.add(v);
            }
        }
        return matches;
    }

    public synchronized Vehicle insertVehicle(Vehicle v) {
        int id = vehicleIdGen.getAndIncrement();
        v.setVehicleId(id);
        vehicles.put(id, v);
        return v;
    }

    public synchronized boolean updateVehicle(Vehicle v) {
        if (vehicles.containsKey(v.getVehicleId())) {
            vehicles.put(v.getVehicleId(), v);
            return true;
        }
        return false;
    }

    public synchronized boolean deleteVehicle(int id) {
        return vehicles.remove(id) != null;
    }

    public synchronized boolean isVehicleCurrentlyParked(int vehicleId) {
        for (ParkingRecord r : records) {
            if (r.getVehicleId() != null && r.getVehicleId() == vehicleId && r.getStatus() == RecordStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // PARKING SLOT OPERATIONS
    // =========================================================================

    public synchronized Optional<ParkingSlot> findSlotById(int slotId) {
        return Optional.ofNullable(slots.get(slotId));
    }

    public synchronized Optional<ParkingSlot> findSlotByNumber(String num) {
        if (num == null) return Optional.empty();
        String target = num.trim().toUpperCase();
        for (ParkingSlot s : slots.values()) {
            if (s.getSlotNumber().equalsIgnoreCase(target)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    public synchronized List<ParkingSlot> findAllSlots() {
        // Enrich slot with current vehicle number if occupied
        List<ParkingSlot> list = new ArrayList<>(slots.values());
        for (ParkingSlot s : list) {
            if (s.isOccupied() && s.getCurrentVehicleId() != null) {
                Vehicle v = vehicles.get(s.getCurrentVehicleId());
                if (v != null) {
                    s.setCurrentVehicleNumber(v.getRegistrationNumber());
                }
            } else if (s.isAvailable()) {
                s.setCurrentVehicleNumber(null);
            }
        }
        return list;
    }

    public synchronized List<ParkingSlot> findSlotsByFloor(int floor) {
        List<ParkingSlot> list = new ArrayList<>();
        for (ParkingSlot s : findAllSlots()) {
            if (s.getFloorNumber() == floor) {
                list.add(s);
            }
        }
        return list;
    }

    public synchronized List<ParkingSlot> findSlotsByStatus(SlotStatus status) {
        List<ParkingSlot> list = new ArrayList<>();
        for (ParkingSlot s : findAllSlots()) {
            if (s.getStatus() == status) {
                list.add(s);
            }
        }
        return list;
    }

    public synchronized List<ParkingSlot> findAvailableSlots(VehicleType vehicleType) {
        List<ParkingSlot> list = new ArrayList<>();
        for (ParkingSlot s : findAllSlots()) {
            if (s.isAvailable() && s.getSlotType().canAccommodate(vehicleType)) {
                list.add(s);
            }
        }
        return list;
    }

    public synchronized Optional<ParkingSlot> findNearestAvailableSlot(VehicleType vehicleType) {
        for (ParkingSlot s : findAllSlots()) {
            if (s.isAvailable() && s.getSlotType().canAccommodate(vehicleType)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    public synchronized boolean updateSlotStatus(int slotId, SlotStatus status, Integer vehicleId) {
        ParkingSlot s = slots.get(slotId);
        if (s != null) {
            s.setStatus(status);
            s.setCurrentVehicleId(vehicleId);
            if (vehicleId != null && vehicles.containsKey(vehicleId)) {
                s.setCurrentVehicleNumber(vehicles.get(vehicleId).getRegistrationNumber());
            } else {
                s.setCurrentVehicleNumber(null);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean insertSlot(ParkingSlot slot) {
        int id = slotIdGen.getAndIncrement();
        slot.setSlotId(id);
        slots.put(id, slot);
        return true;
    }

    public synchronized boolean updateSlot(ParkingSlot slot) {
        if (slots.containsKey(slot.getSlotId())) {
            slots.put(slot.getSlotId(), slot);
            return true;
        }
        return false;
    }

    public synchronized boolean deleteSlot(int slotId) {
        return slots.remove(slotId) != null;
    }

    public synchronized int countTotalSlots() {
        return slots.size();
    }

    public synchronized int countSlotsByStatus(SlotStatus status) {
        int cnt = 0;
        for (ParkingSlot s : slots.values()) {
            if (s.getStatus() == status) cnt++;
        }
        return cnt;
    }

    // =========================================================================
    // PARKING RECORD OPERATIONS
    // =========================================================================

    public synchronized Optional<ParkingRecord> findRecordById(int recordId) {
        for (ParkingRecord r : records) {
            if (r.getRecordId() == recordId) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    public synchronized Optional<ParkingRecord> findActiveRecordByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) return Optional.empty();
        String target = vehicleNumber.trim().toUpperCase();
        for (int i = records.size() - 1; i >= 0; i--) {
            ParkingRecord r = records.get(i);
            if (r.getVehicleNumber().equalsIgnoreCase(target) && r.getStatus() == RecordStatus.ACTIVE) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    public synchronized List<ParkingRecord> findActiveRecords() {
        List<ParkingRecord> list = new ArrayList<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            ParkingRecord r = records.get(i);
            if (r.getStatus() == RecordStatus.ACTIVE) {
                list.add(r);
            }
        }
        return list;
    }

    public synchronized List<ParkingRecord> findAllRecords(int limit) {
        List<ParkingRecord> copy = new ArrayList<>(records);
        Collections.reverse(copy);
        int max = limit > 0 ? Math.min(limit, copy.size()) : copy.size();
        return copy.subList(0, max);
    }

    public synchronized List<ParkingRecord> searchRecords(String keyword, LocalDate fromDate, LocalDate toDate, RecordStatus status) {
        List<ParkingRecord> result = new ArrayList<>();
        String term = keyword != null ? keyword.trim().toLowerCase() : "";

        for (int i = records.size() - 1; i >= 0; i--) {
            ParkingRecord r = records.get(i);

            if (!term.isEmpty()) {
                boolean match = r.getVehicleNumber().toLowerCase().contains(term) ||
                                r.getOwnerName().toLowerCase().contains(term) ||
                                (r.getPhoneNumber() != null && r.getPhoneNumber().contains(term)) ||
                                r.getSlotNumber().toLowerCase().contains(term);
                if (!match) continue;
            }

            if (fromDate != null && r.getEntryTime().toLocalDate().isBefore(fromDate)) {
                continue;
            }
            if (toDate != null && r.getEntryTime().toLocalDate().isAfter(toDate)) {
                continue;
            }
            if (status != null && r.getStatus() != status) {
                continue;
            }

            result.add(r);
        }
        return result;
    }

    public synchronized ParkingRecord insertRecord(ParkingRecord record) {
        int id = recordIdGen.getAndIncrement();
        record.setRecordId(id);
        records.add(record);
        return record;
    }

    public synchronized boolean completeParkingRecord(int recordId, LocalDateTime exitTime, int durationHours, double totalFee) {
        for (ParkingRecord r : records) {
            if (r.getRecordId() == recordId) {
                r.setExitTime(exitTime);
                r.setDurationHours(durationHours);
                r.setTotalFee(totalFee);
                r.setStatus(RecordStatus.COMPLETED);
                return true;
            }
        }
        return false;
    }

    public synchronized int countVehiclesParkedToday() {
        LocalDate today = LocalDate.now();
        int cnt = 0;
        for (ParkingRecord r : records) {
            if (r.getEntryTime() != null && r.getEntryTime().toLocalDate().equals(today)) {
                cnt++;
            }
        }
        return cnt;
    }

    public synchronized int countTotalCompleted() {
        int cnt = 0;
        for (ParkingRecord r : records) {
            if (r.getStatus() == RecordStatus.COMPLETED) {
                cnt++;
            }
        }
        return cnt;
    }

    // =========================================================================
    // PAYMENT & REVENUE OPERATIONS
    // =========================================================================

    public synchronized Payment insertPayment(Payment payment) {
        int id = paymentIdGen.getAndIncrement();
        payment.setPaymentId(id);
        payments.add(payment);
        return payment;
    }

    public synchronized Optional<Payment> findPaymentByRecordId(int recordId) {
        for (int i = payments.size() - 1; i >= 0; i--) {
            Payment p = payments.get(i);
            if (p.getRecordId() == recordId) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public synchronized double calculateTotalRevenue() {
        double sum = 0.0;
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.SUCCESS) {
                sum += p.getAmount();
            }
        }
        return sum;
    }

    public synchronized double calculateTodayRevenue() {
        LocalDate today = LocalDate.now();
        double sum = 0.0;
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.SUCCESS && p.getPaymentTime().toLocalDate().equals(today)) {
                sum += p.getAmount();
            }
        }
        return sum;
    }

    public synchronized double calculateMonthRevenue() {
        LocalDate today = LocalDate.now();
        double sum = 0.0;
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.SUCCESS &&
                p.getPaymentTime().getYear() == today.getYear() &&
                p.getPaymentTime().getMonthValue() == today.getMonthValue()) {
                sum += p.getAmount();
            }
        }
        return sum;
    }

    public synchronized Map<VehicleType, Double> calculateRevenueByVehicleType() {
        Map<VehicleType, Double> map = new EnumMap<>(VehicleType.class);
        for (VehicleType vt : VehicleType.values()) {
            map.put(vt, 0.0);
        }
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.SUCCESS) {
                findRecordById(p.getRecordId()).ifPresent(r -> {
                    VehicleType vt = r.getVehicleType();
                    map.put(vt, map.getOrDefault(vt, 0.0) + p.getAmount());
                });
            }
        }
        return map;
    }

    public synchronized Map<VehicleType, Integer> calculateVehicleCountByType() {
        Map<VehicleType, Integer> map = new EnumMap<>(VehicleType.class);
        for (VehicleType vt : VehicleType.values()) {
            map.put(vt, 0);
        }
        for (ParkingRecord r : records) {
            VehicleType vt = r.getVehicleType();
            map.put(vt, map.getOrDefault(vt, 0) + 1);
        }
        return map;
    }

    public synchronized RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        RevenueReport report = new RevenueReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        double totalRev = 0.0;
        int totalTxn = 0;
        double bikeRev = 0.0; int bikeCnt = 0;
        double carRev = 0.0; int carCnt = 0;
        double truckRev = 0.0; int truckCnt = 0;
        List<ParkingRecord> matchingRecords = new ArrayList<>();

        for (int i = payments.size() - 1; i >= 0; i--) {
            Payment p = payments.get(i);
            if (p.getStatus() != PaymentStatus.SUCCESS) continue;

            LocalDate pDate = p.getPaymentTime().toLocalDate();
            if (startDate != null && pDate.isBefore(startDate)) continue;
            if (endDate != null && pDate.isAfter(endDate)) continue;

            Optional<ParkingRecord> recOpt = findRecordById(p.getRecordId());
            if (recOpt.isPresent()) {
                ParkingRecord r = recOpt.get();
                matchingRecords.add(r);
                totalRev += p.getAmount();
                totalTxn++;

                switch (r.getVehicleType()) {
                    case BIKE -> { bikeRev += p.getAmount(); bikeCnt++; }
                    case CAR -> { carRev += p.getAmount(); carCnt++; }
                    case TRUCK -> { truckRev += p.getAmount(); truckCnt++; }
                }
            }
        }

        report.setTotalRevenue(totalRev);
        report.setTotalTransactions(totalTxn);
        report.setBikeRevenue(bikeRev);
        report.setBikeCount(bikeCnt);
        report.setCarRevenue(carRev);
        report.setCarCount(carCnt);
        report.setTruckRevenue(truckRev);
        report.setTruckCount(truckCnt);
        report.setRecords(matchingRecords);

        return report;
    }
}

