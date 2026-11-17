package com.smartparking.ui.slot;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotType;
import com.smartparking.service.IParkingSlotService;
import com.smartparking.service.ParkingSlotServiceImpl;
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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Dialog for creating a new parking slot.
 */
public class AddSlotDialog extends JDialog {

    private final IParkingSlotService slotService = new ParkingSlotServiceImpl();
    private final JTextField txtSlotNumber = new JTextField(12);
    private final JSpinner spinFloor = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JComboBox<SlotType> cmbType = new JComboBox<>(SlotType.values());
    private boolean slotCreated = false;

    public AddSlotDialog(JFrame parent) {
        super(parent, "Add New Parking Bay", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Create Parking Bay");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Assign bay identifier, floor level, and vehicle category");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Form
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSlotNumber.setBorder(UITheme.BORDER_INPUT);
        txtSlotNumber.setFont(UITheme.FONT_BOLD);
        cmbType.setFont(UITheme.FONT_REGULAR);
        cmbType.setBackground(Color.WHITE);

        addRow(card, gbc, 0, "Bay Number (e.g. A-126):", txtSlotNumber);
        addRow(card, gbc, 1, "Floor Number:", spinFloor);
        addRow(card, gbc, 2, "Vehicle Category:", cmbType);

        add(card, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(UITheme.CONTENT_BG);

        ModernButton btnCreate = new ModernButton("Create Bay", ModernButton.Style.SUCCESS);
        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.Style.SECONDARY);

        btnCreate.addActionListener(e -> handleCreate());
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnCreate);
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

    private void handleCreate() {
        String num = txtSlotNumber.getText().trim();
        int floor = (Integer) spinFloor.getValue();
        SlotType type = (SlotType) cmbType.getSelectedItem();

        try {
            ParkingSlot created = slotService.addSlot(num, floor, type);
            slotCreated = true;
            DialogUtil.showSuccess(this, "Slot " + created.getSlotNumber() + " created successfully!", "Success");
            dispose();
        } catch (Exception ex) {
            DialogUtil.showError(this, ex.getMessage(), "Validation Error");
        }
    }

    public boolean isSlotCreated() {
        return slotCreated;
    }
}
