package com.smartparking.ui.revenue;

import com.smartparking.model.DashboardStats;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RevenueReport;
import com.smartparking.model.VehicleType;
import com.smartparking.service.IRevenueService;
import com.smartparking.service.RevenueServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.ui.common.StatCard;
import com.smartparking.ui.common.StyledTable;
import com.smartparking.util.DateTimeUtil;
import com.smartparking.util.ExportUtil;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Modern financial revenue analytics, category revenue breakdowns, and report generator.
 */
public class RevenuePanel extends JPanel {

    private final IRevenueService revenueService = new RevenueServiceImpl();

    // Top Financial Cards
    private final StatCard cardTotalRev = new StatCard("LIFETIME REVENUE", "₹0.00", "Total collection", UITheme.SUCCESS, "₹");
    private final StatCard cardTodayRev = new StatCard("TODAY'S REVENUE", "₹0.00", "Current day", UITheme.PRIMARY, "₹");
    private final StatCard cardMonthRev = new StatCard("MONTH REVENUE", "₹0.00", "Current month", UITheme.WARNING, "₹");
    private final StatCard cardTotalTxn = new StatCard("SETTLED BILLS", "0", "Completed transactions", new Color(0x63, 0x66, 0xF1), "✓");

    // Breakdown Cards
    private final JLabel lblBikeRev = new JLabel("Bike Revenue: ₹0.00 (0 trips)");
    private final JLabel lblCarRev = new JLabel("Car Revenue: ₹0.00 (0 trips)");
    private final JLabel lblTruckRev = new JLabel("Truck Revenue: ₹0.00 (0 trips)");
    private final JLabel lblAvgRev = new JLabel("Average Fare / Vehicle: ₹0.00");

    // Report Table
    private final DefaultTableModel reportTableModel = new DefaultTableModel(
            new Object[]{"Txn ID", "Plate", "Type", "Slot", "Duration", "Amount Paid", "Settlement Time"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StyledTable tblReport = new StyledTable(reportTableModel);

    private final JComboBox<String> cmbPeriod = new JComboBox<>(new String[]{"Today", "Last 7 Days", "This Month", "All Time"});
    private final JLabel lblReportSummary = new JLabel("Report Summary: 0 Transactions");

    public RevenuePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        refreshReport();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Revenue Intelligence & Financial Reports");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Detailed revenue tracking, tariff realization, and transaction reconciliation");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        ModernButton btnExport = new ModernButton("📥 Export Report", ModernButton.Style.SUCCESS);
        btnExport.setPreferredSize(new Dimension(150, 36));
        btnExport.addActionListener(e -> exportReport());

        ModernButton btnRefresh = new ModernButton("⟳ Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnRefresh.addActionListener(e -> refreshReport());

        headerActions.add(btnExport);
        headerActions.add(btnRefresh);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Container
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new javax.swing.BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. KPI Cards Grid
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        kpiGrid.setOpaque(false);
        kpiGrid.add(cardTotalRev);
        kpiGrid.add(cardTodayRev);
        kpiGrid.add(cardMonthRev);
        kpiGrid.add(cardTotalTxn);
        centerPanel.add(kpiGrid);

        centerPanel.add(javax.swing.Box.createVerticalStrut(15));

        // 2. Category Breakdown Banner Card
        CardPanel breakdownCard = new CardPanel(new GridLayout(1, 4, 15, 10));
        breakdownCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblBikeRev.setFont(UITheme.FONT_BOLD);
        lblBikeRev.setForeground(UITheme.PRIMARY);

        lblCarRev.setFont(UITheme.FONT_BOLD);
        lblCarRev.setForeground(UITheme.SUCCESS);

        lblTruckRev.setFont(UITheme.FONT_BOLD);
        lblTruckRev.setForeground(UITheme.WARNING);

        lblAvgRev.setFont(UITheme.FONT_BOLD);
        lblAvgRev.setForeground(UITheme.TEXT_DARK);

        breakdownCard.add(lblBikeRev);
        breakdownCard.add(lblCarRev);
        breakdownCard.add(lblTruckRev);
        breakdownCard.add(lblAvgRev);

        centerPanel.add(breakdownCard);

        centerPanel.add(javax.swing.Box.createVerticalStrut(15));

        // 3. Transactions Report Card
        CardPanel reportCard = new CardPanel(new BorderLayout(10, 10));
        reportCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel reportHeader = new JPanel(new BorderLayout());
        reportHeader.setOpaque(false);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        JLabel lblFilter = new JLabel("Report Timeframe:");
        lblFilter.setFont(UITheme.FONT_BOLD);
        cmbPeriod.setBackground(Color.WHITE);
        cmbPeriod.addActionListener(e -> refreshReport());

        filterRow.add(lblFilter);
        filterRow.add(cmbPeriod);

        reportHeader.add(filterRow, BorderLayout.WEST);
        reportHeader.add(lblReportSummary, BorderLayout.EAST);
        reportCard.add(reportHeader, BorderLayout.NORTH);

        JScrollPane scrollTable = new JScrollPane(tblReport);
        scrollTable.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        scrollTable.getViewport().setBackground(Color.WHITE);
        scrollTable.setPreferredSize(new Dimension(100, 260));
        reportCard.add(scrollTable, BorderLayout.CENTER);

        centerPanel.add(reportCard);

        JScrollPane mainScroll = new JScrollPane(centerPanel);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScroll, BorderLayout.CENTER);
    }

    public void refreshReport() {
        try {
            DashboardStats stats = revenueService.getDashboardStats();
            cardTotalRev.setValue(String.format("₹%.2f", stats.getTotalRevenue()));
            cardTodayRev.setValue(String.format("₹%.2f", stats.getTodayRevenue()));
            cardMonthRev.setValue(String.format("₹%.2f", stats.getMonthRevenue()));
            cardTotalTxn.setValue(String.valueOf(stats.getTotalCompletedTransactions()));

            // Date Range for report
            String period = (String) cmbPeriod.getSelectedItem();
            LocalDate start = null;
            LocalDate end = LocalDate.now();

            if ("Today".equals(period)) {
                start = LocalDate.now();
            } else if ("Last 7 Days".equals(period)) {
                start = LocalDate.now().minusDays(7);
            } else if ("This Month".equals(period)) {
                start = LocalDate.now().withDayOfMonth(1);
            }

            RevenueReport report = revenueService.generateRevenueReport(start, end);

            lblBikeRev.setText(String.format("🏍 Bike: ₹%.2f (%d)", report.getBikeRevenue(), report.getBikeCount()));
            lblCarRev.setText(String.format("🚗 Car: ₹%.2f (%d)", report.getCarRevenue(), report.getCarCount()));
            lblTruckRev.setText(String.format("🚚 Truck: ₹%.2f (%d)", report.getTruckRevenue(), report.getTruckCount()));

            double avg = report.getTotalTransactions() > 0 ? (report.getTotalRevenue() / report.getTotalTransactions()) : 0.0;
            lblAvgRev.setText(String.format("Avg Ticket: ₹%.2f", avg));

            lblReportSummary.setText(String.format("Period Revenue: ₹%.2f | Settled: %d Txns",
                    report.getTotalRevenue(), report.getTotalTransactions()));

            // Populate Report Table
            reportTableModel.setRowCount(0);
            for (ParkingRecord r : report.getRecords()) {
                reportTableModel.addRow(new Object[]{
                        "INV-" + String.format("%05d", r.getRecordId()),
                        r.getVehicleNumber(),
                        r.getVehicleType().name(),
                        r.getSlotNumber(),
                        r.getFormattedDuration(),
                        String.format("₹%.2f", r.getTotalFee() != null ? r.getTotalFee() : 0.0),
                        DateTimeUtil.formatDateTime(r.getExitTime())
                });
            }

        } catch (Exception ex) {
            System.err.println("Failed to refresh revenue report: " + ex.getMessage());
        }
    }

    private void exportReport() {
        if (tblReport.getRowCount() == 0) {
            DialogUtil.showWarning(this, "No transaction records available to export.", "Empty Report");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Financial Revenue Report");
        chooser.setSelectedFile(new File("revenue_report_" + System.currentTimeMillis() + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Comma Separated (*.csv)", "csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".csv")) {
                target = new File(target.getAbsolutePath() + ".csv");
            }
            try {
                ExportUtil.exportTableToCSV(tblReport, target);
                DialogUtil.showSuccess(this, "Financial report successfully saved to:\n" + target.getAbsolutePath(), "Export Complete");
            } catch (IOException ex) {
                DialogUtil.showError(this, "Failed to save file: " + ex.getMessage(), "Export Error");
            }
        }
    }
}
