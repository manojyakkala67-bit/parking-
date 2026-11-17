package com.smartparking.ui;

import com.smartparking.model.Admin;
import com.smartparking.service.AuthServiceImpl;
import com.smartparking.service.IAuthService;
import com.smartparking.ui.auth.LoginFrame;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.dashboard.DashboardPanel;
import com.smartparking.ui.entry.VehicleEntryPanel;
import com.smartparking.ui.exit.VehicleExitPanel;
import com.smartparking.ui.history.ParkingHistoryPanel;
import com.smartparking.ui.revenue.RevenuePanel;
import com.smartparking.ui.settings.ChangePasswordDialog;
import com.smartparking.ui.slot.SlotManagementPanel;
import com.smartparking.ui.vehicle.VehicleManagementPanel;
import com.smartparking.util.DateTimeUtil;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Application Frame containing the Dark Navy Sidebar, Top Status Bar, and CardLayout screens.
 * Operates in Standalone mode with Zero external Database requirement.
 */
public class MainFrame extends JFrame {

    private final Admin currentAdmin;
    private final IAuthService authService = new AuthServiceImpl();

    // Content Cards
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    // Screen Views
    private DashboardPanel dashboardPanel;
    private VehicleEntryPanel entryPanel;
    private VehicleExitPanel exitPanel;
    private SlotManagementPanel slotPanel;
    private VehicleManagementPanel vehiclePanel;
    private ParkingHistoryPanel historyPanel;
    private RevenuePanel revenuePanel;

    // Header Labels
    private final JLabel lblCurrentTitle = new JLabel("Operations Dashboard");
    private final JLabel lblLiveClock = new JLabel();
    private final JLabel lblSystemStatus = new JLabel("● Storage: Ready (Embedded)");

    private final List<SidebarButton> navButtons = new ArrayList<>();

    public MainFrame(Admin admin) {
        super("Smart Parking Management System - Standalone Edition");
        this.currentAdmin = admin;
        initUI();
        startClock();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Sidebar (Dark Navy Slate)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Right Side: Top Status Header + Content Area
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UITheme.CONTENT_BG);

        JPanel topHeader = createTopHeader();
        rightContainer.add(topHeader, BorderLayout.NORTH);

        // Screens
        dashboardPanel = new DashboardPanel();
        entryPanel = new VehicleEntryPanel();
        exitPanel = new VehicleExitPanel();
        slotPanel = new SlotManagementPanel();
        vehiclePanel = new VehicleManagementPanel();
        historyPanel = new ParkingHistoryPanel();
        revenuePanel = new RevenuePanel();

        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(entryPanel, "ENTRY");
        contentCards.add(exitPanel, "EXIT");
        contentCards.add(slotPanel, "SLOTS");
        contentCards.add(vehiclePanel, "VEHICLES");
        contentCards.add(historyPanel, "HISTORY");
        contentCards.add(revenuePanel, "REVENUE");

        rightContainer.add(contentCards, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);

        // Select default tab
        selectNav("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));

        // Brand Banner
        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 3));
        brand.setBackground(UITheme.SIDEBAR_BG);
        brand.setBorder(BorderFactory.createEmptyBorder(22, 20, 20, 20));

        JLabel lblLogo = new JLabel("🅿 SMART PARKING");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("MANAGEMENT SYSTEM");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        brand.add(lblLogo);
        brand.add(lblSub);
        sidebar.add(brand, BorderLayout.NORTH);

        // Navigation Menu Items
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false);
        menu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addNavButton(menu, "📊  Dashboard", "DASHBOARD", "Operations Dashboard");
        addNavButton(menu, "🚗  Vehicle Entry", "ENTRY", "Vehicle Inward Entry Gate");
        addNavButton(menu, "🏁  Exit & Billing", "EXIT", "Vehicle Exit & Billing Desk");
        addNavButton(menu, "🅿  Parking Bays", "SLOTS", "Parking Bay Management");
        addNavButton(menu, "📋  Vehicle Registry", "VEHICLES", "Vehicle Directory");
        addNavButton(menu, "📜  Audit History", "HISTORY", "Parking Audit Trail");
        addNavButton(menu, "💰  Revenue Reports", "REVENUE", "Revenue Analytics & Reports");

        menu.add(Box.createVerticalStrut(15));
        JLabel lblAdminSection = new JLabel("  SECURITY & SETTINGS");
        lblAdminSection.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblAdminSection.setForeground(UITheme.SIDEBAR_MUTED);
        menu.add(lblAdminSection);
        menu.add(Box.createVerticalStrut(5));

        SidebarButton btnPwd = new SidebarButton("🔑  Change Password", false);
        btnPwd.addActionListener(e -> {
            ChangePasswordDialog dlg = new ChangePasswordDialog(this);
            dlg.setVisible(true);
        });
        menu.add(btnPwd);

        sidebar.add(menu, BorderLayout.CENTER);

        // Footer User Profile & Logout
        JPanel userFooter = new JPanel(new BorderLayout());
        userFooter.setBackground(UITheme.SIDEBAR_ACTIVE);
        userFooter.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel userText = new JPanel(new GridLayout(2, 1, 0, 2));
        userText.setOpaque(false);

        String name = currentAdmin != null ? currentAdmin.getFullName() : "Administrator";
        JLabel lblUser = new JLabel(name);
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(Color.WHITE);

        String role = currentAdmin != null ? currentAdmin.getRole() : "ADMIN";
        JLabel lblRole = new JLabel(role);
        lblRole.setFont(UITheme.FONT_SMALL);
        lblRole.setForeground(UITheme.SIDEBAR_MUTED);

        userText.add(lblUser);
        userText.add(lblRole);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(UITheme.FONT_SMALL);
        btnLogout.setForeground(UITheme.DANGER);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());

        userFooter.add(userText, BorderLayout.CENTER);
        userFooter.add(btnLogout, BorderLayout.EAST);

        sidebar.add(userFooter, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavButton(JPanel container, String text, String cardKey, String headerTitle) {
        SidebarButton btn = new SidebarButton(text, false);
        btn.addActionListener(e -> {
            selectNav(cardKey);
            lblCurrentTitle.setText(headerTitle);

            switch (cardKey) {
                case "DASHBOARD" -> dashboardPanel.refreshData();
                case "SLOTS" -> slotPanel.refreshSlots();
                case "HISTORY" -> historyPanel.loadHistory();
                case "REVENUE" -> revenuePanel.refreshReport();
            }
        });
        navButtons.add(btn);
        btn.putClientProperty("CARD_KEY", cardKey);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    private void selectNav(String cardKey) {
        cardLayout.show(contentCards, cardKey);
        for (SidebarButton b : navButtons) {
            String k = (String) b.getClientProperty("CARD_KEY");
            b.setActive(cardKey.equals(k));
        }
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(14, 25, 14, 25)
        ));

        lblCurrentTitle.setFont(UITheme.FONT_HEADER);
        lblCurrentTitle.setForeground(UITheme.TEXT_DARK);
        header.add(lblCurrentTitle, BorderLayout.WEST);

        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightStatus.setOpaque(false);

        lblLiveClock.setFont(UITheme.FONT_BOLD);
        lblLiveClock.setForeground(UITheme.TEXT_SECONDARY);

        lblSystemStatus.setFont(UITheme.FONT_BADGE);
        lblSystemStatus.setForeground(UITheme.SUCCESS);

        rightStatus.add(lblLiveClock);
        rightStatus.add(lblSystemStatus);
        header.add(rightStatus, BorderLayout.EAST);

        return header;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblLiveClock.setText(now.format(DateTimeUtil.DISPLAY_DATETIME));
        });
        timer.start();
    }

    private void handleLogout() {
        if (DialogUtil.showConfirm(this, "Are you sure you want to log out of the administration panel?", "Logout Confirmation")) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }

    private static class SidebarButton extends JButton {
        private boolean isActive;

        public SidebarButton(String text, boolean isActive) {
            super(text);
            this.isActive = isActive;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(UITheme.FONT_BOLD);
            setForeground(isActive ? Color.WHITE : UITheme.SIDEBAR_TEXT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(220, 40));
            setPreferredSize(new Dimension(220, 40));
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? Color.WHITE : UITheme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(UITheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (getModel().isRollover()) {
                g2.setColor(UITheme.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
