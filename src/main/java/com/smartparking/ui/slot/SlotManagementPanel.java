package com.smartparking.ui.slot;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.SlotType;
import com.smartparking.service.IParkingSlotService;
import com.smartparking.service.ParkingSlotServiceImpl;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Visual interactive grid panel for managing and inspecting parking slots.
 * Color-coded: Green = Available, Red = Occupied, Yellow = Maintenance.
 */
public class SlotManagementPanel extends JPanel {

    private final IParkingSlotService slotService = new ParkingSlotServiceImpl();

    private final JComboBox<String> cmbFloorFilter = new JComboBox<>(new String[]{"All Floors", "Floor 1", "Floor 2"});
    private final JComboBox<String> cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "AVAILABLE", "OCCUPIED", "MAINTENANCE"});
    private final JComboBox<String> cmbTypeFilter = new JComboBox<>(new String[]{"All Types", "BIKE", "CAR", "TRUCK"});

    private final JPanel gridPanel = new JPanel();
    private final JLabel lblStats = new JLabel("Total: 0 | Available: 0 | Occupied: 0");

    public SlotManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        refreshSlots();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel lblTitle = new JLabel("Parking Bay Management & Visual Map");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Interactive real-time spatial bay grid. Click any slot to view telemetry or toggle maintenance");
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        ModernButton btnAdd = new ModernButton("+ Add New Bay", ModernButton.Style.PRIMARY);
        btnAdd.setPreferredSize(new Dimension(150, 36));
        btnAdd.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddSlotDialog dlg = new AddSlotDialog(topFrame);
            dlg.setVisible(true);
            if (dlg.isSlotCreated()) {
                refreshSlots();
            }
        });

        ModernButton btnRefresh = new ModernButton("⟳ Refresh", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnRefresh.addActionListener(e -> refreshSlots());

        rightActions.add(btnAdd);
        rightActions.add(btnRefresh);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(rightActions, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Filter Bar Card
        CardPanel filterCard = new CardPanel(new BorderLayout(10, 10));
        filterCard.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filters.setOpaque(false);

        filters.add(new JLabel("Floor:"));
        cmbFloorFilter.setBackground(Color.WHITE);
        cmbFloorFilter.addActionListener(e -> refreshSlots());
        filters.add(cmbFloorFilter);

        filters.add(new JLabel("Status:"));
        cmbStatusFilter.setBackground(Color.WHITE);
        cmbStatusFilter.addActionListener(e -> refreshSlots());
        filters.add(cmbStatusFilter);

        filters.add(new JLabel("Vehicle Category:"));
        cmbTypeFilter.setBackground(Color.WHITE);
        cmbTypeFilter.addActionListener(e -> refreshSlots());
        filters.add(cmbTypeFilter);

        filterCard.add(filters, BorderLayout.WEST);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        legend.setOpaque(false);
        legend.add(createLegendBadge("Available", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS));
        legend.add(createLegendBadge("Occupied", UITheme.DANGER_LIGHT, UITheme.DANGER));
        legend.add(createLegendBadge("Maintenance", UITheme.WARNING_LIGHT, UITheme.WARNING));
        filterCard.add(legend, BorderLayout.EAST);

        // Center: Grid of Slots
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(filterCard, BorderLayout.NORTH);

        gridPanel.setOpaque(false);
        gridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JScrollPane scrollGrid = new JScrollPane(gridPanel);
        scrollGrid.setBorder(BorderFactory.createEmptyBorder());
        scrollGrid.setOpaque(false);
        scrollGrid.getViewport().setOpaque(false);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);

        centerContainer.add(scrollGrid, BorderLayout.CENTER);

        // Bottom Stats Bar
        lblStats.setFont(UITheme.FONT_BOLD);
        lblStats.setForeground(UITheme.TEXT_SECONDARY);
        lblStats.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerContainer.add(lblStats, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JLabel createLegendBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(UITheme.FONT_BADGE);
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createLineBorder(fg, 1));
        return lbl;
    }

    public void refreshSlots() {
        gridPanel.removeAll();

        List<ParkingSlot> allSlots = slotService.getAllSlots();

        String floorSel = (String) cmbFloorFilter.getSelectedItem();
        String statusSel = (String) cmbStatusFilter.getSelectedItem();
        String typeSel = (String) cmbTypeFilter.getSelectedItem();

        int availCount = 0;
        int occupiedCount = 0;
        int maintCount = 0;
        int displayCount = 0;

        for (ParkingSlot s : allSlots) {
            // Stats
            if (s.isAvailable()) availCount++;
            else if (s.isOccupied()) occupiedCount++;
            else if (s.isUnderMaintenance()) maintCount++;

            // Filter checks
            if (floorSel != null && !floorSel.equals("All Floors")) {
                int floorNum = Integer.parseInt(floorSel.replace("Floor ", ""));
                if (s.getFloorNumber() != floorNum) continue;
            }
            if (statusSel != null && !statusSel.equals("All Statuses")) {
                if (!s.getStatus().name().equals(statusSel)) continue;
            }
            if (typeSel != null && !typeSel.equals("All Types")) {
                if (!s.getSlotType().name().equals(typeSel) && s.getSlotType() != SlotType.GENERAL) continue;
            }

            displayCount++;
            gridPanel.add(createSlotWidget(s));
        }

        lblStats.setText(String.format("Showing %d Bays | Total Bays: %d | Available: %d (Green) | Occupied: %d (Red) | Maintenance: %d (Amber)",
                displayCount, allSlots.size(), availCount, occupiedCount, maintCount));

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createSlotWidget(ParkingSlot slot) {
        Color bgColor;
        Color fgColor;
        Color borderColor;

        if (slot.isAvailable()) {
            bgColor = UITheme.SUCCESS_LIGHT;
            fgColor = UITheme.SUCCESS_HOVER;
            borderColor = UITheme.SUCCESS;
        } else if (slot.isOccupied()) {
            bgColor = UITheme.DANGER_LIGHT;
            fgColor = UITheme.DANGER_HOVER;
            borderColor = UITheme.DANGER;
        } else {
            bgColor = UITheme.WARNING_LIGHT;
            fgColor = UITheme.WARNING_HOVER;
            borderColor = UITheme.WARNING;
        }

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(135, 95));
        btn.setLayout(new GridLayout(3, 1, 0, 2));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblNum = new JLabel(slot.getSlotNumber(), JLabel.CENTER);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNum.setForeground(fgColor);

        JLabel lblType = new JLabel(slot.getSlotType().getDisplayName(), JLabel.CENTER);
        lblType.setFont(UITheme.FONT_SMALL);
        lblType.setForeground(UITheme.TEXT_SECONDARY);

        String bottomText;
        if (slot.isOccupied()) {
            bottomText = slot.getCurrentVehicleNumber() != null ? slot.getCurrentVehicleNumber() : "OCCUPIED";
        } else if (slot.isUnderMaintenance()) {
            bottomText = "MAINTENANCE";
        } else {
            bottomText = "VACANT";
        }
        JLabel lblStatus = new JLabel(bottomText, JLabel.CENTER);
        lblStatus.setFont(UITheme.FONT_BADGE);
        lblStatus.setForeground(fgColor);

        btn.add(lblNum);
        btn.add(lblType);
        btn.add(lblStatus);

        btn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            SlotDetailDialog dlg = new SlotDetailDialog(topFrame, slot);
            dlg.setVisible(true);
            if (dlg.isDataChanged()) {
                refreshSlots();
            }
        });

        return btn;
    }
}
