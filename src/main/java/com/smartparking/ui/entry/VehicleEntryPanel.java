package com.smartparking.ui.entry;

import com.smartparking.model.ParkingRecord;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;
import com.smartparking.service.IParkingService;
import com.smartparking.service.IParkingSlotService;
import com.smartparking.service.IVehicleService;
import com.smartparking.service.ParkingServiceImpl;
import com.smartparking.service.ParkingSlotServiceImpl;
import com.smartparking.service.VehicleServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.DuplicateVehicleException;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;

/**
 * Modern form for vehicle entry intake, automated slot allocation, and entry pass generation.
 */
public class VehicleEntryPanel extends JPanel {

    private final IParkingService parkingService = new ParkingServiceImpl();
    private final IVehicleService vehicleService = new VehicleServiceImpl();
    private final IParkingSlotService slotService = new ParkingSlotServiceImpl();

    private final JTextField txtRegNo = new JTextField(16);
    private final JComboBox<VehicleType> cmbType = new JComboBox<>(VehicleType.values());
    private final JTextField txtOwnerName = new JTextField(16);
    private final JTextField txtPhone = new JTextField(16);
    private final JCheckBox chkAutoSlot = new JCheckBox("Automatically assign nearest available slot", true);
    private final JComboBox<String> cmbManualSlots = new JComboBox<>();
    private final JLabel lblSlotPreview = new JLabel("Nearest Slot: Checking...");

    // Right card labels
    private final JLabel lblBikeAvail = new JLabel("Bike Slots Free: -");
    private final JLabel lblCarAvail = new JLabel("Car Slots Free: -");
    private final JLabel lblTruckAvail = new JLabel("Truck Slots Free: -");

    public VehicleEntryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        refreshAvailability();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Vehicle Inward Entry Gate");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Capture vehicle details, verify registration, and allocate nearest parking bay");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);
        headerPanel.add(titleBlock, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center split: Form on Left, Rate Card & Live Slot Availability on Right
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);

        // LEFT: Entry Form Card
        CardPanel formCard = new CardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRegNo.setBorder(UITheme.BORDER_INPUT);
        txtRegNo.setFont(UITheme.FONT_BOLD);
        txtOwnerName.setBorder(UITheme.BORDER_INPUT);
        txtPhone.setBorder(UITheme.BORDER_INPUT);
        cmbType.setFont(UITheme.FONT_REGULAR);
        cmbType.setBackground(Color.WHITE);

        // Row 0: Reg Number + Lookup button
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblReg = new JLabel("Vehicle Plate No *:");
        lblReg.setFont(UITheme.FONT_BOLD);
        formCard.add(lblReg, gbc);

        JPanel regRow = new JPanel(new BorderLayout(5, 0));
        regRow.setOpaque(false);
        regRow.add(txtRegNo, BorderLayout.CENTER);
        ModernButton btnLookup = new ModernButton("Lookup", ModernButton.Style.OUTLINE);
        btnLookup.setPreferredSize(new Dimension(85, 36));
        btnLookup.addActionListener(e -> lookupVehicle());
        regRow.add(btnLookup, BorderLayout.EAST);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formCard.add(regRow, gbc);

        // Row 1: Vehicle Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblT = new JLabel("Vehicle Type *:");
        lblT.setFont(UITheme.FONT_BOLD);
        formCard.add(lblT, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        formCard.add(cmbType, gbc);

        // Row 2: Owner Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblO = new JLabel("Owner Full Name *:");
        lblO.setFont(UITheme.FONT_BOLD);
        formCard.add(lblO, gbc);

        gbc.gridx = 1;
        formCard.add(txtOwnerName, gbc);

        // Row 3: Phone
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblP = new JLabel("Mobile Number *:");
        lblP.setFont(UITheme.FONT_BOLD);
        formCard.add(lblP, gbc);

        gbc.gridx = 1;
        formCard.add(txtPhone, gbc);

        // Row 4: Auto slot allocation checkbox
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        chkAutoSlot.setFont(UITheme.FONT_REGULAR);
        chkAutoSlot.setOpaque(false);
        chkAutoSlot.addActionListener(e -> toggleSlotMode());
        formCard.add(chkAutoSlot, gbc);

        // Row 5: Slot Preview / Manual Selector
        gbc.gridy = 5;
        lblSlotPreview.setFont(UITheme.FONT_BOLD);
        lblSlotPreview.setForeground(UITheme.SUCCESS);
        formCard.add(lblSlotPreview, gbc);

        gbc.gridy = 6;
        cmbManualSlots.setFont(UITheme.FONT_REGULAR);
        cmbManualSlots.setBackground(Color.WHITE);
        cmbManualSlots.setVisible(false);
        formCard.add(cmbManualSlots, gbc);

        // Row 7: Action Button
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 6, 6, 6);
        ModernButton btnAdmit = new ModernButton("✓ Admit Vehicle & Issue Pass", ModernButton.Style.SUCCESS);
        btnAdmit.setPreferredSize(new Dimension(280, 42));
        btnAdmit.addActionListener(e -> processEntry());
        formCard.add(btnAdmit, gbc);

        centerGrid.add(formCard);

        // RIGHT: Tariff Rate Card & Live Slot Availability
        JPanel rightCol = new JPanel(new GridLayout(2, 1, 0, 15));
        rightCol.setOpaque(false);

        // Rate Card
        CardPanel rateCard = new CardPanel(new BorderLayout(10, 10));
        rateCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblRateTitle = new JLabel("Parking Tariff Schedule");
        lblRateTitle.setFont(UITheme.FONT_HEADER);
        lblRateTitle.setForeground(UITheme.TEXT_DARK);
        rateCard.add(lblRateTitle, BorderLayout.NORTH);

        JPanel rateGrid = new JPanel(new GridLayout(4, 1, 0, 8));
        rateGrid.setOpaque(false);

        rateGrid.add(createTariffRow("🏍 Two Wheeler (Bike)", "₹40.00 / hour", UITheme.PRIMARY));
        rateGrid.add(createTariffRow("🚗 Four Wheeler (Car)", "₹100.00 / hour", UITheme.SUCCESS));
        rateGrid.add(createTariffRow("🚚 Heavy Vehicle (Truck)", "₹150.00 / hour", UITheme.WARNING));

        JLabel lblRule = new JLabel("<html><small>• Billing Rule: Started hours rounded up. Minimum 1 hour charge applies.</small></html>");
        lblRule.setForeground(UITheme.TEXT_MUTED);
        rateGrid.add(lblRule);

        rateCard.add(rateGrid, BorderLayout.CENTER);
        rightCol.add(rateCard);

        // Availability Card
        CardPanel availCard = new CardPanel(new BorderLayout(10, 10));
        availCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblAvailTitle = new JLabel("Live Parking Capacity");
        lblAvailTitle.setFont(UITheme.FONT_HEADER);
        lblAvailTitle.setForeground(UITheme.TEXT_DARK);
        availCard.add(lblAvailTitle, BorderLayout.NORTH);

        JPanel availGrid = new JPanel(new GridLayout(3, 1, 0, 8));
        availGrid.setOpaque(false);

        lblBikeAvail.setFont(UITheme.FONT_BOLD);
        lblBikeAvail.setForeground(UITheme.PRIMARY);
        lblCarAvail.setFont(UITheme.FONT_BOLD);
        lblCarAvail.setForeground(UITheme.SUCCESS);
        lblTruckAvail.setFont(UITheme.FONT_BOLD);
        lblTruckAvail.setForeground(UITheme.WARNING);

        availGrid.add(lblBikeAvail);
        availGrid.add(lblCarAvail);
        availGrid.add(lblTruckAvail);

        availCard.add(availGrid, BorderLayout.CENTER);
        rightCol.add(availCard);

        centerGrid.add(rightCol);

        JScrollPane scroll = new JScrollPane(centerGrid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        // Listen for type change to re-query nearest slot
        cmbType.addActionListener(e -> updateSlotPreview());
        updateSlotPreview();
    }

    private JPanel createTariffRow(String category, String rate, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lblCat = new JLabel(category);
        lblCat.setFont(UITheme.FONT_BOLD);
        lblCat.setForeground(UITheme.TEXT_DARK);

        JLabel lblR = new JLabel(rate);
        lblR.setFont(UITheme.FONT_BOLD);
        lblR.setForeground(color);

        row.add(lblCat, BorderLayout.WEST);
        row.add(lblR, BorderLayout.EAST);
        return row;
    }

    private void lookupVehicle() {
        String regNo = txtRegNo.getText().trim();
        if (regNo.isEmpty()) {
            DialogUtil.showWarning(this, "Please enter a registration plate number to lookup.", "Lookup");
            return;
        }

        Optional<Vehicle> opt = vehicleService.getVehicleByRegNo(regNo);
        if (opt.isPresent()) {
            Vehicle v = opt.get();
            txtOwnerName.setText(v.getOwnerName());
            txtPhone.setText(v.getPhoneNumber());
            cmbType.setSelectedItem(v.getVehicleType());
            DialogUtil.showSuccess(this, "Vehicle details found and auto-filled!", "Found");
        } else {
            DialogUtil.showWarning(this, "Vehicle not yet registered. Enter details to auto-register.", "New Vehicle");
        }
        updateSlotPreview();
    }

    private void toggleSlotMode() {
        boolean auto = chkAutoSlot.isSelected();
        lblSlotPreview.setVisible(auto);
        cmbManualSlots.setVisible(!auto);

        if (!auto) {
            populateManualSlots();
        }
    }

    private void populateManualSlots() {
        cmbManualSlots.removeAllItems();
        VehicleType type = (VehicleType) cmbType.getSelectedItem();
        List<ParkingSlot> avail = slotService.getAvailableSlots(type);
        for (ParkingSlot s : avail) {
            cmbManualSlots.addItem(s.getSlotNumber() + " (Floor " + s.getFloorNumber() + ")");
        }
        if (cmbManualSlots.getItemCount() == 0) {
            cmbManualSlots.addItem("NO COMPATIBLE SLOTS AVAILABLE");
        }
    }

    private void updateSlotPreview() {
        VehicleType type = (VehicleType) cmbType.getSelectedItem();
        if (type == null) return;

        Optional<ParkingSlot> nearest = slotService.getNearestAvailableSlot(type);
        if (nearest.isPresent()) {
            ParkingSlot s = nearest.get();
            lblSlotPreview.setText("Auto-assigned Slot: " + s.getSlotNumber() + " (Floor " + s.getFloorNumber() + ")");
            lblSlotPreview.setForeground(UITheme.SUCCESS);
        } else {
            lblSlotPreview.setText("NO AVAILABLE SLOTS FOR " + type.getDisplayName().toUpperCase());
            lblSlotPreview.setForeground(UITheme.DANGER);
        }

        if (!chkAutoSlot.isSelected()) {
            populateManualSlots();
        }
    }

    private void refreshAvailability() {
        try {
            int bikeSlots = slotService.getAvailableSlots(VehicleType.BIKE).size();
            int carSlots = slotService.getAvailableSlots(VehicleType.CAR).size();
            int truckSlots = slotService.getAvailableSlots(VehicleType.TRUCK).size();

            lblBikeAvail.setText("🏍 Available Bike Bays:   " + bikeSlots);
            lblCarAvail.setText("🚗 Available Car Bays:    " + carSlots);
            lblTruckAvail.setText("🚚 Available Truck Bays:  " + truckSlots);
        } catch (Exception ignored) {}
    }

    private void processEntry() {
        String reg = txtRegNo.getText().trim();
        VehicleType type = (VehicleType) cmbType.getSelectedItem();
        String owner = txtOwnerName.getText().trim();
        String phone = txtPhone.getText().trim();

        Integer chosenSlotId = null;
        if (!chkAutoSlot.isSelected()) {
            String selected = (String) cmbManualSlots.getSelectedItem();
            if (selected == null || selected.contains("NO COMPATIBLE")) {
                DialogUtil.showError(this, "No valid slot selected.", "Validation");
                return;
            }
            String slotNum = selected.split(" ")[0];
            Optional<ParkingSlot> sOpt = slotService.getSlotByNumber(slotNum);
            if (sOpt.isPresent()) {
                chosenSlotId = sOpt.get().getSlotId();
            }
        }

        try {
            ParkingRecord record = parkingService.processVehicleEntry(reg, type, owner, phone, chosenSlotId);

            // Pop up ticket
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            EntryPassDialog passDialog = new EntryPassDialog(topFrame, record);
            passDialog.setVisible(true);

            // Clear inputs
            txtRegNo.setText("");
            txtOwnerName.setText("");
            txtPhone.setText("");
            updateSlotPreview();
            refreshAvailability();

        } catch (DuplicateVehicleException dup) {
            DialogUtil.showWarning(this, dup.getMessage(), "Duplicate Entry Blocked");
        } catch (Exception ex) {
            DialogUtil.showError(this, ex.getMessage(), "Entry Error");
        }
    }
}
