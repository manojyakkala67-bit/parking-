package com.smartparking.ui.exit;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.Payment;
import com.smartparking.model.PaymentMethod;
import com.smartparking.service.IParkingService;
import com.smartparking.service.ParkingServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.ui.common.StyledTable;
import com.smartparking.util.DateTimeUtil;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Modern panel for managing vehicle exits, automated fee calculation, and payment processing.
 */
public class VehicleExitPanel extends JPanel {

    private final IParkingService parkingService = new ParkingServiceImpl();

    // Table of active vehicles
    private final DefaultTableModel activeTableModel = new DefaultTableModel(
            new Object[]{"Record ID", "Plate Number", "Type", "Slot", "Entry Time"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StyledTable tblActive = new StyledTable(activeTableModel);

    // Search
    private final JTextField txtSearchPlate = new JTextField(12);

    // Bill Summary Labels
    private final JLabel lblSelectedPlate = new JLabel("-");
    private final JLabel lblSelectedType = new JLabel("-");
    private final JLabel lblSelectedSlot = new JLabel("-");
    private final JLabel lblEntryTime = new JLabel("-");
    private final JLabel lblExitTime = new JLabel("-");
    private final JLabel lblDuration = new JLabel("-");
    private final JLabel lblRate = new JLabel("-");
    private final JLabel lblTotalDue = new JLabel("₹0.00");

    // Payment Controls
    private final JComboBox<PaymentMethod> cmbPaymentMethod = new JComboBox<>(PaymentMethod.values());
    private final JTextField txtAmountTendered = new JTextField(10);
    private final JLabel lblChange = new JLabel("Change: ₹0.00");

    private ParkingRecord currentSelectedRecord = null;
    private double currentTotalFee = 0.0;

    public VehicleExitPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        loadActiveParkings();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Vehicle Exit & Billing Desk");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Select an active parked vehicle, compute billable duration, collect fee, and release slot");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        ModernButton btnRefresh = new ModernButton("⟳ Refresh Active Cars", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(180, 36));
        btnRefresh.addActionListener(e -> loadActiveParkings());

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Split: Left = Active Vehicles Table, Right = Settlement Bill Card
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);

        // LEFT: Active Table Card
        CardPanel leftCard = new CardPanel(new BorderLayout(10, 10));
        leftCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("Filter Plate:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        txtSearchPlate.setBorder(UITheme.BORDER_INPUT);
        txtSearchPlate.setPreferredSize(new Dimension(140, 34));
        txtSearchPlate.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterActive(); }
            public void removeUpdate(DocumentEvent e) { filterActive(); }
            public void changedUpdate(DocumentEvent e) { filterActive(); }
        });

        ModernButton btnFind = new ModernButton("Select", ModernButton.Style.PRIMARY);
        btnFind.setPreferredSize(new Dimension(90, 34));
        btnFind.addActionListener(e -> findAndSelectPlate());

        searchBar.add(lblSearch);
        searchBar.add(txtSearchPlate);
        searchBar.add(btnFind);
        leftCard.add(searchBar, BorderLayout.NORTH);

        tblActive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblActive.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblActive.getSelectedRow() != -1) {
                int row = tblActive.getSelectedRow();
                int recordId = Integer.parseInt(tblActive.getValueAt(row, 0).toString());
                loadBillDetails(recordId);
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblActive);
        scrollTable.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        scrollTable.getViewport().setBackground(Color.WHITE);
        leftCard.add(scrollTable, BorderLayout.CENTER);

        centerGrid.add(leftCard);

        // RIGHT: Billing & Settlement Card
        CardPanel billCard = new CardPanel(new BorderLayout(10, 10));
        billCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblBillTitle = new JLabel("Fare Calculation & Settlement");
        lblBillTitle.setFont(UITheme.FONT_HEADER);
        lblBillTitle.setForeground(UITheme.TEXT_DARK);
        billCard.add(lblBillTitle, BorderLayout.NORTH);

        JPanel billDetails = new JPanel(new GridBagLayout());
        billDetails.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addDetailRow(billDetails, gbc, 0, "Vehicle Number:", lblSelectedPlate);
        addDetailRow(billDetails, gbc, 1, "Vehicle Type:", lblSelectedType);
        addDetailRow(billDetails, gbc, 2, "Parking Slot:", lblSelectedSlot);
        addDetailRow(billDetails, gbc, 3, "Check-in Time:", lblEntryTime);
        addDetailRow(billDetails, gbc, 4, "Current Time:", lblExitTime);
        addDetailRow(billDetails, gbc, 5, "Total Duration:", lblDuration);
        addDetailRow(billDetails, gbc, 6, "Applicable Rate:", lblRate);

        // Total Fee Highlight Box
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JPanel feeBox = new JPanel(new BorderLayout());
        feeBox.setBackground(UITheme.PRIMARY_LIGHT);
        feeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblDueTitle = new JLabel("TOTAL FARE DUE:");
        lblDueTitle.setFont(UITheme.FONT_BOLD);
        lblDueTitle.setForeground(UITheme.PRIMARY);

        lblTotalDue.setFont(UITheme.FONT_KPI_VAL);
        lblTotalDue.setForeground(UITheme.PRIMARY);
        lblTotalDue.setHorizontalAlignment(SwingConstants.RIGHT);

        feeBox.add(lblDueTitle, BorderLayout.WEST);
        feeBox.add(lblTotalDue, BorderLayout.EAST);
        billDetails.add(feeBox, gbc);

        // Payment Mode
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblMode = new JLabel("Payment Mode:");
        lblMode.setFont(UITheme.FONT_BOLD);
        billDetails.add(lblMode, gbc);

        gbc.gridx = 1;
        cmbPaymentMethod.setFont(UITheme.FONT_REGULAR);
        cmbPaymentMethod.setBackground(Color.WHITE);
        billDetails.add(cmbPaymentMethod, gbc);

        // Tendered Amount
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblTender = new JLabel("Amount Received (₹):");
        lblTender.setFont(UITheme.FONT_BOLD);
        billDetails.add(lblTender, gbc);

        gbc.gridx = 1;
        txtAmountTendered.setBorder(UITheme.BORDER_INPUT);
        txtAmountTendered.setFont(UITheme.FONT_BOLD);
        txtAmountTendered.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateChange(); }
            public void removeUpdate(DocumentEvent e) { calculateChange(); }
            public void changedUpdate(DocumentEvent e) { calculateChange(); }
        });
        billDetails.add(txtAmountTendered, gbc);

        // Change label
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        lblChange.setFont(UITheme.FONT_BOLD);
        lblChange.setForeground(UITheme.TEXT_SECONDARY);
        billDetails.add(lblChange, gbc);

        // Process Exit Button
        gbc.gridy = 11;
        gbc.insets = new Insets(12, 5, 5, 5);
        ModernButton btnProcess = new ModernButton("💳 Process Exit & Release Slot", ModernButton.Style.SUCCESS);
        btnProcess.setPreferredSize(new Dimension(280, 42));
        btnProcess.addActionListener(e -> processExit());
        billDetails.add(btnProcess, gbc);

        billCard.add(billDetails, BorderLayout.CENTER);
        centerGrid.add(billCard);

        add(centerGrid, BorderLayout.CENTER);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valLabel) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        valLabel.setFont(UITheme.FONT_REGULAR);
        valLabel.setForeground(UITheme.TEXT_DARK);
        panel.add(valLabel, gbc);
    }

    private void loadActiveParkings() {
        activeTableModel.setRowCount(0);
        try {
            List<ParkingRecord> activeList = parkingService.getActiveParkings();
            for (ParkingRecord r : activeList) {
                activeTableModel.addRow(new Object[]{
                        r.getRecordId(),
                        r.getVehicleNumber(),
                        r.getVehicleType().name(),
                        r.getSlotNumber(),
                        DateTimeUtil.formatDateTime(r.getEntryTime())
                });
            }
        } catch (Exception ex) {
            System.err.println("Failed to load active parkings: " + ex.getMessage());
        }
    }

    private void filterActive() {
        String filter = txtSearchPlate.getText().trim().toUpperCase();
        if (filter.isEmpty()) {
            loadActiveParkings();
            return;
        }

        activeTableModel.setRowCount(0);
        try {
            List<ParkingRecord> activeList = parkingService.getActiveParkings();
            for (ParkingRecord r : activeList) {
                if (r.getVehicleNumber().toUpperCase().contains(filter) || r.getSlotNumber().toUpperCase().contains(filter)) {
                    activeTableModel.addRow(new Object[]{
                            r.getRecordId(),
                            r.getVehicleNumber(),
                            r.getVehicleType().name(),
                            r.getSlotNumber(),
                            DateTimeUtil.formatDateTime(r.getEntryTime())
                    });
                }
            }
        } catch (Exception ignored) {}
    }

    private void findAndSelectPlate() {
        String query = txtSearchPlate.getText().trim().toUpperCase();
        if (query.isEmpty()) {
            DialogUtil.showWarning(this, "Please enter a vehicle plate number to find.", "Search");
            return;
        }

        Optional<ParkingRecord> opt = parkingService.getActiveParking(query);
        if (opt.isPresent()) {
            loadBillDetails(opt.get().getRecordId());
        } else {
            DialogUtil.showWarning(this, "No active parking record found for plate: " + query, "Not Found");
        }
    }

    private void loadBillDetails(int recordId) {
        Optional<ParkingRecord> opt = parkingService.getParkingRecordById(recordId);
        if (opt.isEmpty()) return;

        currentSelectedRecord = opt.get();
        LocalDateTime now = LocalDateTime.now();
        long billableHours = currentSelectedRecord.calculateBillableHours(now);
        currentTotalFee = parkingService.calculateEstimatedFee(currentSelectedRecord, now);

        lblSelectedPlate.setText(currentSelectedRecord.getVehicleNumber());
        lblSelectedType.setText(currentSelectedRecord.getVehicleType().getDisplayName());
        lblSelectedSlot.setText(currentSelectedRecord.getSlotNumber());
        lblEntryTime.setText(DateTimeUtil.formatDateTime(currentSelectedRecord.getEntryTime()));
        lblExitTime.setText(DateTimeUtil.formatDateTime(now));
        lblDuration.setText(DateTimeUtil.formatDuration(currentSelectedRecord.getEntryTime(), now) + 
                " (" + billableHours + " started hr" + (billableHours == 1 ? "" : "s") + ")");
        lblRate.setText("₹" + (int) currentSelectedRecord.getVehicleType().getHourlyRate() + " / hour");
        lblTotalDue.setText(String.format("₹%.2f", currentTotalFee));

        txtAmountTendered.setText(String.format("%.2f", currentTotalFee));
        calculateChange();
    }

    private void calculateChange() {
        try {
            double tendered = Double.parseDouble(txtAmountTendered.getText().trim());
            double change = tendered - currentTotalFee;
            if (change >= 0) {
                lblChange.setText(String.format("Change to Return: ₹%.2f", change));
                lblChange.setForeground(UITheme.SUCCESS);
            } else {
                lblChange.setText(String.format("Short by: ₹%.2f", Math.abs(change)));
                lblChange.setForeground(UITheme.DANGER);
            }
        } catch (NumberFormatException e) {
            lblChange.setText("Change: ₹0.00");
            lblChange.setForeground(UITheme.TEXT_SECONDARY);
        }
    }

    private void processExit() {
        if (currentSelectedRecord == null) {
            DialogUtil.showWarning(this, "Please select an active vehicle to settle.", "No Vehicle Selected");
            return;
        }

        double tendered;
        try {
            tendered = Double.parseDouble(txtAmountTendered.getText().trim());
        } catch (NumberFormatException e) {
            DialogUtil.showError(this, "Please enter a valid numeric payment amount.", "Invalid Payment");
            return;
        }

        if (tendered < currentTotalFee) {
            DialogUtil.showError(this, String.format("Insufficient payment. Received ₹%.2f but ₹%.2f is required.", tendered, currentTotalFee), "Payment Required");
            return;
        }

        PaymentMethod method = (PaymentMethod) cmbPaymentMethod.getSelectedItem();

        try {
            Payment payment = parkingService.processVehicleExit(currentSelectedRecord.getRecordId(), method, tendered);

            // Pop up invoice dialog
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExitInvoiceDialog invoiceDlg = new ExitInvoiceDialog(topFrame, currentSelectedRecord, payment);
            invoiceDlg.setVisible(true);

            // Reset selection and reload
            currentSelectedRecord = null;
            lblSelectedPlate.setText("-");
            lblSelectedType.setText("-");
            lblSelectedSlot.setText("-");
            lblEntryTime.setText("-");
            lblExitTime.setText("-");
            lblDuration.setText("-");
            lblRate.setText("-");
            lblTotalDue.setText("₹0.00");
            txtAmountTendered.setText("");
            lblChange.setText("Change: ₹0.00");

            loadActiveParkings();

        } catch (Exception ex) {
            DialogUtil.showError(this, "Error processing exit: " + ex.getMessage(), "Error");
        }
    }
}
