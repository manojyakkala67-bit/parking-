package com.smartparking.ui.vehicle;

import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;
import com.smartparking.service.IVehicleService;
import com.smartparking.service.VehicleServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Dialog for Registering and Editing Vehicles in the system.
 */
public class VehicleDialog extends JDialog {

    private final IVehicleService vehicleService = new VehicleServiceImpl();
    private final Vehicle vehicleToEdit;
    private final JTextField txtRegNo = new JTextField(15);
    private final JComboBox<VehicleType> cmbType = new JComboBox<>(VehicleType.values());
    private final JTextField txtOwnerName = new JTextField(15);
    private final JTextField txtPhone = new JTextField(15);
    private boolean saved = false;

    public VehicleDialog(JFrame parent, Vehicle vehicleToEdit) {
        super(parent, vehicleToEdit == null ? "Register New Vehicle" : "Edit Vehicle Details", true);
        this.vehicleToEdit = vehicleToEdit;
        initUI();
    }

    private void initUI() {
        setSize(460, 360);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(vehicleToEdit == null ? "Vehicle Registration" : "Update Vehicle Information");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Maintain vehicle registration record and ownership details");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Form Card
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRegNo.setBorder(UITheme.BORDER_INPUT);
        txtOwnerName.setBorder(UITheme.BORDER_INPUT);
        txtPhone.setBorder(UITheme.BORDER_INPUT);
        cmbType.setFont(UITheme.FONT_REGULAR);
        cmbType.setBackground(Color.WHITE);

        if (vehicleToEdit != null) {
            txtRegNo.setText(vehicleToEdit.getRegistrationNumber());
            txtRegNo.setEditable(false);
            cmbType.setSelectedItem(vehicleToEdit.getVehicleType());
            txtOwnerName.setText(vehicleToEdit.getOwnerName());
            txtPhone.setText(vehicleToEdit.getPhoneNumber());
        }

        addRow(card, gbc, 0, "Registration Number *:", txtRegNo);
        addRow(card, gbc, 1, "Vehicle Type *:", cmbType);
        addRow(card, gbc, 2, "Owner Name *:", txtOwnerName);
        addRow(card, gbc, 3, "Mobile Number *:", txtPhone);

        add(card, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(UITheme.CONTENT_BG);

        ModernButton btnSave = new ModernButton(vehicleToEdit == null ? "Register Vehicle" : "Save Changes", ModernButton.Style.SUCCESS);
        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.Style.SECONDARY);

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnSave);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        panel.add(comp, gbc);
    }

    private void handleSave() {
        String reg = txtRegNo.getText().trim();
        VehicleType type = (VehicleType) cmbType.getSelectedItem();
        String owner = txtOwnerName.getText().trim();
        String phone = txtPhone.getText().trim();

        try {
            if (vehicleToEdit == null) {
                vehicleService.registerVehicle(reg, type, owner, phone);
                DialogUtil.showSuccess(this, "Vehicle " + reg + " registered successfully!", "Success");
            } else {
                vehicleService.updateVehicle(vehicleToEdit.getVehicleId(), reg, type, owner, phone);
                DialogUtil.showSuccess(this, "Vehicle details updated successfully!", "Updated");
            }
            saved = true;
            dispose();
        } catch (Exception ex) {
            DialogUtil.showError(this, ex.getMessage(), "Error");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
