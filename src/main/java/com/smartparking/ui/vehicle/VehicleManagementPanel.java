package com.smartparking.ui.vehicle;

import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;
import com.smartparking.service.IVehicleService;
import com.smartparking.service.VehicleServiceImpl;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Optional;

/**
 * Modern management interface for registering, updating, searching, and deleting vehicles.
 */
public class VehicleManagementPanel extends JPanel {

    private final IVehicleService vehicleService = new VehicleServiceImpl();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Plate Number", "Type", "Owner Name", "Phone", "Registration Date"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StyledTable tblVehicles = new StyledTable(tableModel);

    private final JTextField txtSearch = new JTextField(16);
    private final JComboBox<String> cmbTypeFilter = new JComboBox<>(new String[]{"All Types", "BIKE", "CAR", "TRUCK"});
    private final JLabel lblCount = new JLabel("Total Registered Vehicles: 0");

    public VehicleManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        loadVehicles();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Vehicle Directory & Registration");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Maintain records for registered bikes, cars, and heavy commercial vehicles");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        ModernButton btnAdd = new ModernButton("+ Register Vehicle", ModernButton.Style.PRIMARY);
        btnAdd.setPreferredSize(new Dimension(170, 36));
        btnAdd.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            VehicleDialog dlg = new VehicleDialog(topFrame, null);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadVehicles();
            }
        });

        ModernButton btnRefresh = new ModernButton("⟳ Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnRefresh.addActionListener(e -> loadVehicles());

        headerActions.add(btnAdd);
        headerActions.add(btnRefresh);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Card
        CardPanel card = new CardPanel(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setPreferredSize(new Dimension(180, 34));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadVehicles(); }
            public void removeUpdate(DocumentEvent e) { loadVehicles(); }
            public void changedUpdate(DocumentEvent e) { loadVehicles(); }
        });

        JLabel lblType = new JLabel("Category:");
        lblType.setFont(UITheme.FONT_BOLD);
        cmbTypeFilter.setBackground(Color.WHITE);
        cmbTypeFilter.addActionListener(e -> loadVehicles());

        leftFilters.add(lblSearch);
        leftFilters.add(txtSearch);
        leftFilters.add(lblType);
        leftFilters.add(cmbTypeFilter);

        toolbar.add(leftFilters, BorderLayout.WEST);
        toolbar.add(lblCount, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        // Table
        tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTable = new JScrollPane(tblVehicles);
        scrollTable.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER));
        scrollTable.getViewport().setBackground(Color.WHITE);
        card.add(scrollTable, BorderLayout.CENTER);

        // Bottom Actions Bar
        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomActions.setOpaque(false);

        ModernButton btnEdit = new ModernButton("Edit Details", ModernButton.Style.SECONDARY);
        btnEdit.setPreferredSize(new Dimension(130, 36));
        btnEdit.addActionListener(e -> editSelectedVehicle());

        ModernButton btnDelete = new ModernButton("Delete Record", ModernButton.Style.DANGER);
        btnDelete.setPreferredSize(new Dimension(130, 36));
        btnDelete.addActionListener(e -> deleteSelectedVehicle());

        bottomActions.add(btnEdit);
        bottomActions.add(btnDelete);
        card.add(bottomActions, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void loadVehicles() {
        tableModel.setRowCount(0);
        String query = txtSearch.getText().trim();
        String typeFilter = (String) cmbTypeFilter.getSelectedItem();

        try {
            List<Vehicle> list = vehicleService.searchVehicles(query);
            int count = 0;
            for (Vehicle v : list) {
                if (typeFilter != null && !typeFilter.equals("All Types")) {
                    if (!v.getVehicleType().name().equals(typeFilter)) continue;
                }
                tableModel.addRow(new Object[]{
                        v.getVehicleId(),
                        v.getRegistrationNumber(),
                        v.getVehicleType().name(),
                        v.getOwnerName(),
                        v.getPhoneNumber(),
                        DateTimeUtil.formatDateTime(v.getCreatedAt())
                });
                count++;
            }
            lblCount.setText("Showing " + count + " of " + list.size() + " Vehicles");
        } catch (Exception ex) {
            System.err.println("Failed to load vehicles: " + ex.getMessage());
        }
    }

    private void editSelectedVehicle() {
        int row = tblVehicles.getSelectedRow();
        if (row == -1) {
            DialogUtil.showWarning(this, "Please select a vehicle row from the table to edit.", "Selection");
            return;
        }

        int vehicleId = Integer.parseInt(tblVehicles.getValueAt(row, 0).toString());
        String regNo = tblVehicles.getValueAt(row, 1).toString();

        Optional<Vehicle> opt = vehicleService.getVehicleByRegNo(regNo);
        if (opt.isPresent()) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            VehicleDialog dlg = new VehicleDialog(topFrame, opt.get());
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                loadVehicles();
            }
        }
    }

    private void deleteSelectedVehicle() {
        int row = tblVehicles.getSelectedRow();
        if (row == -1) {
            DialogUtil.showWarning(this, "Please select a vehicle row from the table to delete.", "Selection");
            return;
        }

        int vehicleId = Integer.parseInt(tblVehicles.getValueAt(row, 0).toString());
        String regNo = tblVehicles.getValueAt(row, 1).toString();

        if (DialogUtil.showConfirm(this, "Are you sure you want to delete vehicle " + regNo + "?\nThis action cannot be undone.", "Confirm Deletion")) {
            try {
                boolean ok = vehicleService.deleteVehicle(vehicleId);
                if (ok) {
                    DialogUtil.showSuccess(this, "Vehicle " + regNo + " deleted successfully.", "Deleted");
                    loadVehicles();
                } else {
                    DialogUtil.showError(this, "Could not delete vehicle record.", "Error");
                }
            } catch (Exception ex) {
                DialogUtil.showError(this, ex.getMessage(), "Deletion Blocked");
            }
        }
    }
}
