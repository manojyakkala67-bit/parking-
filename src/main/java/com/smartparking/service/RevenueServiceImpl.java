package com.smartparking.service;

import com.smartparking.dao.IParkingRecordDAO;
import com.smartparking.dao.IParkingSlotDAO;
import com.smartparking.dao.IPaymentDAO;
import com.smartparking.dao.ParkingRecordDAOImpl;
import com.smartparking.dao.ParkingSlotDAOImpl;
import com.smartparking.dao.PaymentDAOImpl;
import com.smartparking.model.DashboardStats;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RecordStatus;
import com.smartparking.model.RevenueReport;
import com.smartparking.model.SlotStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of IRevenueService.
 */
public class RevenueServiceImpl implements IRevenueService {

    private final IParkingSlotDAO slotDAO;
    private final IParkingRecordDAO recordDAO;
    private final IPaymentDAO paymentDAO;

    public RevenueServiceImpl() {
        this.slotDAO = new ParkingSlotDAOImpl();
        this.recordDAO = new ParkingRecordDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    public RevenueServiceImpl(IParkingSlotDAO slotDAO, IParkingRecordDAO recordDAO, IPaymentDAO paymentDAO) {
        this.slotDAO = slotDAO;
        this.recordDAO = recordDAO;
        this.paymentDAO = paymentDAO;
    }

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Slot stats
        int totalSlots = slotDAO.countTotalSlots();
        int availableSlots = slotDAO.countSlotsByStatus(SlotStatus.AVAILABLE);
        int occupiedSlots = slotDAO.countSlotsByStatus(SlotStatus.OCCUPIED);
        int maintenanceSlots = slotDAO.countSlotsByStatus(SlotStatus.MAINTENANCE);

        stats.setTotalSlots(totalSlots);
        stats.setAvailableSlots(availableSlots);
        stats.setOccupiedSlots(occupiedSlots);
        stats.setMaintenanceSlots(maintenanceSlots);
        stats.setTotalVehiclesCurrentlyParked(occupiedSlots);

        // Revenue stats
        stats.setTotalRevenue(paymentDAO.calculateTotalRevenue());
        stats.setTodayRevenue(paymentDAO.calculateTodayRevenue());
        stats.setMonthRevenue(paymentDAO.calculateMonthRevenue());

        // Parking counts
        stats.setVehiclesParkedToday(recordDAO.countVehiclesParkedToday());
        stats.setTotalCompletedTransactions(recordDAO.countTotalCompleted());

        // Breakdowns
        stats.setVehicleCountByType(paymentDAO.calculateVehicleCountByType());
        stats.setRevenueByType(paymentDAO.calculateRevenueByVehicleType());

        return stats;
    }

    @Override
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        return paymentDAO.generateReport(startDate, endDate);
    }

    @Override
    public List<ParkingRecord> searchHistory(String keyword, LocalDate from, LocalDate to, RecordStatus status) {
        return recordDAO.search(keyword, from, to, status);
    }

    @Override
    public List<ParkingRecord> getRecentTransactions(int limit) {
        return recordDAO.findRecentRecords(limit);
    }
}
