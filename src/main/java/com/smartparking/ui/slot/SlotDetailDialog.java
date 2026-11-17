package com.smartparking.ui.slot;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.service.IParkingSlotService;
import com.smartparking.service.ParkingSlotServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Modal dialog for inspecting and modifying a selected parking slot.
 */
public class SlotDetailDialog extends JDialog {

    private final ParkingSlot slot;
    private final IParkingSlotService slotService = new ParkingSlotServiceImpl();
    private boolean dataChanged = false;

    public SlotDetailDialog(JFrame parent, ParkingSlot slot) {
        super(parent, "Parking Bay Details - " + slot.getSlotNumber(), true);
        this.slot = slot;
        initUI();
    }

    private void initUI() {
        setSize(440, 360);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Bay: " + slot.getSlotNumber());
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Floor " + slot.getFloorNumber() + " | " + slot.getSlotType().getDisplayName());
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Info Card
        CardPanel card = new CardPanel(new GridLayout(5, 2, 10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        addInfoRow(card, "Slot Identifier:", slot.getSlotNumber());
        addInfoRow(card, "Floor Level:", "Floor " + slot.getFloorNumber());
        addInfoRow(card, "Category:", slot.getSlotType().getDisplayName());
        addInfoRow(card, "Current Status:", slot.getStatus().name());
        addInfoRow(card, "Parked Vehicle:", slot.getCurrentVehicleNumber() != null ? slot.getCurrentVehicleNumber() : "None (Vacant)");

        add(card, BorderLayout.CENTER);

        // Action Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(UITheme.CONTENT_BG);

        ModernButton btnToggleMaint = new ModernButton(
                slot.isUnderMaintenance() ? "Set Available" : "Set Maintenance",
                ModernButton.Style.WARNING
        );
        btnToggleMaint.setEnabled(!slot.isOccupied());
        btnToggleMaint.addActionListener(e -> {
            try {
                slotService.toggleMaintenance(slot.getSlotId());
                dataChanged = true;
                DialogUtil.showSuccess(this, "Bay status toggled successfully.", "Updated");
                dispose();
            } catch (Exception ex) {
                DialogUtil.showError(this, ex.getMessage(), "Error");
            }
        });

        ModernButton btnDelete = new ModernButton("Delete Slot", ModernButton.Style.DANGER);
        btnDelete.setEnabled(!slot.isOccupied());
        btnDelete.addActionListener(e -> {
            if (DialogUtil.showConfirm(this, "Are you sure you want to delete Slot " + slot.getSlotNumber() + "?", "Delete Slot")) {
                try {
                    slotService.deleteSlot(slot.getSlotId());
                    dataChanged = true;
                    DialogUtil.showSuccess(this, "Slot deleted successfully.", "Deleted");
                    dispose();
                } catch (Exception ex) {
                    DialogUtil.showError(this, ex.getMessage(), "Error");
                }
            }
        });

        ModernButton btnClose = new ModernButton("Close", ModernButton.Style.SECONDARY);
        btnClose.addActionListener(e -> dispose());

        footer.add(btnToggleMaint);
        footer.add(btnDelete);
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblL = new JLabel(label);
        lblL.setFont(UITheme.FONT_BOLD);
        lblL.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lblL);

        JLabel lblV = new JLabel(value);
        lblV.setFont(UITheme.FONT_REGULAR);
        lblV.setForeground(UITheme.TEXT_DARK);
        panel.add(lblV);
    }

    public boolean isDataChanged() {
        return dataChanged;
    }
}
