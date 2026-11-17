package com.smartparking.ui.dashboard;

import com.smartparking.model.DashboardStats;
import com.smartparking.model.ParkingRecord;
import com.smartparking.service.IRevenueService;
import com.smartparking.service.RevenueServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.CustomBarChart;
import com.smartparking.ui.common.CustomDonutChart;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.ui.common.StatCard;
import com.smartparking.ui.common.StyledTable;
import com.smartparking.util.DateTimeUtil;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Modern Dashboard View displaying real-time statistics, graphs, and recent activity.
 */
public class DashboardPanel extends JPanel {

    private final IRevenueService revenueService = new RevenueServiceImpl();

    private final StatCard cardTotalSlots = new StatCard("TOTAL SLOTS", "0", "Capacity", UITheme.PRIMARY, "🅿");
    private final StatCard cardAvailable = new StatCard("AVAILABLE SLOTS", "0", "Ready to park", UITheme.SUCCESS, "✓");
    private final StatCard cardOccupied = new StatCard("OCCUPIED SLOTS", "0", "In use", UITheme.DANGER, "🚗");
    private final StatCard cardParked = new StatCard("CURRENTLY PARKED", "0", "Active vehicles", new Color(0x63, 0x66, 0xF1), "🔒");
    private final StatCard cardTodayRevenue = new StatCard("TODAY'S REVENUE", "₹0.00", "Collected today", UITheme.WARNING, "₹");
    private final StatCard cardTodayVehicles = new StatCard("VEHICLES TODAY", "0", "Entries today", new Color(0x06, 0xB6, 0xD4), "📅");

    private final CustomDonutChart donutChart = new CustomDonutChart();
    private final CustomBarChart barChart = new CustomBarChart();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Record ID", "Vehicle Plate", "Type", "Owner", "Slot", "Entry Time", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StyledTable tblRecent = new StyledTable(tableModel);

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        refreshData();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Operations Dashboard");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Live real-time parking utilization, telemetry, and revenue overview");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        ModernButton btnRefresh = new ModernButton("⟳ Refresh Live Data", ModernButton.Style.PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(170, 38));
        btnRefresh.addActionListener(e -> refreshData());

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Scrollable
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // 1. KPI Cards Row (2 rows of 3, or 1 row of 6)
        JPanel kpiGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        kpiGrid.setOpaque(false);
        kpiGrid.add(cardTotalSlots);
        kpiGrid.add(cardAvailable);
        kpiGrid.add(cardOccupied);
        kpiGrid.add(cardParked);
        kpiGrid.add(cardTodayRevenue);
        kpiGrid.add(cardTodayVehicles);
        contentPanel.add(kpiGrid);

        contentPanel.add(javax.swing.Box.createVerticalStrut(20));

        // 2. Charts Row (Donut Chart + Bar Chart)
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 15, 15));
        chartsRow.setOpaque(false);

        // Left Chart Card: Occupancy
        CardPanel cardChart1 = new CardPanel(new BorderLayout(10, 10));
        cardChart1.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblChart1Title = new JLabel("Parking Slot Occupancy");
        lblChart1Title.setFont(UITheme.FONT_HEADER);
        lblChart1Title.setForeground(UITheme.TEXT_DARK);
        cardChart1.add(lblChart1Title, BorderLayout.NORTH);
        cardChart1.add(donutChart, BorderLayout.CENTER);

        // Right Chart Card: Vehicle Type Distribution
        CardPanel cardChart2 = new CardPanel(new BorderLayout(10, 10));
        cardChart2.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblChart2Title = new JLabel("Vehicle Category Distribution");
        lblChart2Title.setFont(UITheme.FONT_HEADER);
        lblChart2Title.setForeground(UITheme.TEXT_DARK);
        cardChart2.add(lblChart2Title, BorderLayout.NORTH);
        cardChart2.add(barChart, BorderLayout.CENTER);

        chartsRow.add(cardChart1);
        chartsRow.add(cardChart2);
        contentPanel.add(chartsRow);

        contentPanel.add(javax.swing.Box.createVerticalStrut(20));

        // 3. Recent Activity Feed
        CardPanel recentCard = new CardPanel(new BorderLayout(10, 10));
        recentCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblRecentTitle = new JLabel("Recent Activity Stream");
        lblRecentTitle.setFont(UITheme.FONT_HEADER);
        lblRecentTitle.setForeground(UITheme.TEXT_DARK);
        recentCard.add(lblRecentTitle, BorderLayout.NORTH);

        JScrollPane scrollTable = new JScrollPane(tblRecent);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(Color.WHITE);
        scrollTable.setPreferredSize(new Dimension(100, 200));
        recentCard.add(scrollTable, BorderLayout.CENTER);

        contentPanel.add(recentCard);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScroll, BorderLayout.CENTER);
    }

    public void refreshData() {
        try {
            DashboardStats stats = revenueService.getDashboardStats();

            cardTotalSlots.setValue(String.valueOf(stats.getTotalSlots()));
            cardAvailable.setValue(String.valueOf(stats.getAvailableSlots()));
            cardAvailable.setSubtitle(String.format("%.1f%% capacity free", 
                    stats.getTotalSlots() > 0 ? (stats.getAvailableSlots() * 100.0 / stats.getTotalSlots()) : 0.0));

            cardOccupied.setValue(String.valueOf(stats.getOccupiedSlots()));
            cardOccupied.setSubtitle(String.format("%.1f%% occupied", stats.getOccupancyPercentage()));

            cardParked.setValue(String.valueOf(stats.getTotalVehiclesCurrentlyParked()));
            cardTodayRevenue.setValue(String.format("₹%.2f", stats.getTodayRevenue()));
            cardTodayRevenue.setSubtitle(String.format("Month: ₹%.2f", stats.getMonthRevenue()));

            cardTodayVehicles.setValue(String.valueOf(stats.getVehiclesParkedToday()));
            cardTodayVehicles.setSubtitle(String.format("Total: %d served", stats.getTotalCompletedTransactions()));

            donutChart.updateData(stats.getAvailableSlots(), stats.getOccupiedSlots(), stats.getMaintenanceSlots());
            barChart.updateData(stats.getVehicleCountByType());

            // Refresh recent table
            tableModel.setRowCount(0);
            List<ParkingRecord> recents = revenueService.getRecentTransactions(8);
            for (ParkingRecord r : recents) {
                tableModel.addRow(new Object[]{
                        "#" + r.getRecordId(),
                        r.getVehicleNumber(),
                        r.getVehicleType().name(),
                        r.getOwnerName(),
                        r.getSlotNumber(),
                        DateTimeUtil.formatDateTime(r.getEntryTime()),
                        r.getStatus().name()
                });
            }
        } catch (Exception ex) {
            System.err.println("Dashboard refresh error: " + ex.getMessage());
        }
    }
}
