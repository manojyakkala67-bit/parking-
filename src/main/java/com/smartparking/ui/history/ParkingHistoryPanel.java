package com.smartparking.ui.history;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.RecordStatus;
import com.smartparking.service.IRevenueService;
import com.smartparking.service.RevenueServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * Audit log and historical parking transactions panel with filtering and CSV export.
 */
public class ParkingHistoryPanel extends JPanel {

    private final IRevenueService revenueService = new RevenueServiceImpl();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Record ID", "Plate No", "Type", "Owner Name", "Slot", "Entry Time", "Exit Time", "Duration", "Amount Paid", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StyledTable tblHistory = new StyledTable(tableModel);

    private final JTextField txtSearch = new JTextField(15);
    private final JComboBox<String> cmbDateFilter = new JComboBox<>(new String[]{"All Time", "Today", "Last 7 Days", "This Month"});
    private final JComboBox<String> cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "ACTIVE", "COMPLETED"});
    private final JLabel lblSummary = new JLabel("Records: 0");

    public ParkingHistoryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        loadHistory();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Parking Transactions Audit Trail");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Comprehensive history of entries, exits, durations, tariffs, and settlement receipts");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        ModernButton btnExport = new ModernButton("📥 Export to CSV", ModernButton.Style.SUCCESS);
        btnExport.setPreferredSize(new Dimension(150, 36));
        btnExport.addActionListener(e -> exportToCsv());

        ModernButton btnRefresh = new ModernButton("⟳ Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnRefresh.addActionListener(e -> loadHistory());

        headerActions.add(btnExport);
        headerActions.add(btnRefresh);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Card
        CardPanel card = new CardPanel(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setPreferredSize(new Dimension(160, 34));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadHistory(); }
            public void removeUpdate(DocumentEvent e) { loadHistory(); }
            public void changedUpdate(DocumentEvent e) { loadHistory(); }
        });

        JLabel lblDate = new JLabel("Time Period:");
        lblDate.setFont(UITheme.FONT_BOLD);
        cmbDateFilter.setBackground(Color.WHITE);
        cmbDateFilter.addActionListener(e -> loadHistory());

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(UITheme.FONT_BOLD);
        cmbStatusFilter.setBackground(Color.WHITE);
        cmbStatusFilter.addActionListener(e -> loadHistory());

        filters.add(lblSearch);
        filters.add(txtSearch);
        filters.add(lblDate);
        filters.add(cmbDateFilter);
        filters.add(lblStatus);
        filters.add(cmbStatusFilter);

        toolbar.add(filters, BorderLayout.WEST);
        toolbar.add(lblSummary, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        // History Table
        JScrollPane scrollTable = new JScrollPane(tblHistory);
        scrollTable.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        scrollTable.getViewport().setBackground(Color.WHITE);
        card.add(scrollTable, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void loadHistory() {
        tableModel.setRowCount(0);
        String search = txtSearch.getText().trim();
        String dateOption = (String) cmbDateFilter.getSelectedItem();
        String statusOption = (String) cmbStatusFilter.getSelectedItem();

        LocalDate fromDate = null;
        LocalDate toDate = LocalDate.now();

        if ("Today".equals(dateOption)) {
            fromDate = LocalDate.now();
        } else if ("Last 7 Days".equals(dateOption)) {
            fromDate = LocalDate.now().minusDays(7);
        } else if ("This Month".equals(dateOption)) {
            fromDate = LocalDate.now().withDayOfMonth(1);
        }

        RecordStatus status = null;
        if (statusOption != null && !statusOption.equals("All Statuses")) {
            status = RecordStatus.valueOf(statusOption);
        }

        try {
            List<ParkingRecord> list = revenueService.searchHistory(search, fromDate, toDate, status);
            double totalRev = 0.0;
            for (ParkingRecord r : list) {
                double fee = r.getTotalFee() != null ? r.getTotalFee() : 0.0;
                totalRev += fee;
                tableModel.addRow(new Object[]{
                        "#" + r.getRecordId(),
                        r.getVehicleNumber(),
                        r.getVehicleType().name(),
                        r.getOwnerName(),
                        r.getSlotNumber(),
                        DateTimeUtil.formatDateTime(r.getEntryTime()),
                        r.getExitTime() != null ? DateTimeUtil.formatDateTime(r.getExitTime()) : "Parked (In Progress)",
                        r.getFormattedDuration(),
                        String.format("₹%.2f", fee),
                        r.getStatus().name()
                });
            }
            lblSummary.setText(String.format("Found %d Records | Period Revenue: ₹%.2f", list.size(), totalRev));
        } catch (Exception ex) {
            System.err.println("Failed to load parking history: " + ex.getMessage());
        }
    }

    private void exportToCsv() {
        if (tblHistory.getRowCount() == 0) {
            DialogUtil.showWarning(this, "No records to export.", "Empty Table");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Parking History CSV");
        chooser.setSelectedFile(new File("parking_history_" + System.currentTimeMillis() + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Comma Separated (*.csv)", "csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".csv")) {
                target = new File(target.getAbsolutePath() + ".csv");
            }
            try {
                ExportUtil.exportTableToCSV(tblHistory, target);
                DialogUtil.showSuccess(this, "Parking history exported successfully to:\n" + target.getAbsolutePath(), "Export Success");
            } catch (IOException ex) {
                DialogUtil.showError(this, "Export error: " + ex.getMessage(), "Error");
            }
        }
    }
}
