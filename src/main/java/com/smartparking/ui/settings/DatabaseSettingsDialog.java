package com.smartparking.ui.settings;

import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.DatabaseConnection;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Modern modal dialog for configuring MySQL database connection and initializing schema.
 */
public class DatabaseSettingsDialog extends JDialog {

    private final JTextField txtHost = new JTextField(15);
    private final JTextField txtPort = new JTextField(6);
    private final JTextField txtDatabase = new JTextField(15);
    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JLabel lblStatus = new JLabel("Status: Ready");

    public DatabaseSettingsDialog(JFrame parent) {
        super(parent, "Database Configuration & Setup", true);
        initUI();
    }

    private void initUI() {
        setSize(520, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SIDEBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("MySQL Database Settings");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Configure MySQL connection parameters and initialize database tables");
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.SIDEBAR_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form Card
        CardPanel formCard = new CardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtHost.setText(DatabaseConnection.getHost());
        txtPort.setText(DatabaseConnection.getPort());
        txtDatabase.setText(DatabaseConnection.getDatabase());
        txtUsername.setText(DatabaseConnection.getUsername());
        txtPassword.setText(DatabaseConnection.getPassword());

        txtHost.setBorder(UITheme.BORDER_INPUT);
        txtPort.setBorder(UITheme.BORDER_INPUT);
        txtDatabase.setBorder(UITheme.BORDER_INPUT);
        txtUsername.setBorder(UITheme.BORDER_INPUT);
        txtPassword.setBorder(UITheme.BORDER_INPUT);

        // Add inputs
        addRow(formCard, gbc, 0, "Host / Server:", txtHost);
        addRow(formCard, gbc, 1, "Port:", txtPort);
        addRow(formCard, gbc, 2, "Database Name:", txtDatabase);
        addRow(formCard, gbc, 3, "Username:", txtUsername);
        addRow(formCard, gbc, 4, "Password:", txtPassword);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        lblStatus.setFont(UITheme.FONT_BOLD);
        lblStatus.setForeground(UITheme.TEXT_SECONDARY);
        formCard.add(lblStatus, gbc);

        add(formCard, BorderLayout.CENTER);

        // Footer Action Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footerPanel.setBackground(UITheme.CONTENT_BG);

        ModernButton btnTest = new ModernButton("Test Connection", ModernButton.Style.OUTLINE);
        ModernButton btnInit = new ModernButton("Initialize Tables", ModernButton.Style.WARNING);
        ModernButton btnSave = new ModernButton("Save & Connect", ModernButton.Style.PRIMARY);
        ModernButton btnCancel = new ModernButton("Close", ModernButton.Style.SECONDARY);

        btnTest.addActionListener(e -> testConnection());
        btnInit.addActionListener(e -> initializeTables());
        btnSave.addActionListener(e -> saveSettings());
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnTest);
        footerPanel.add(btnInit);
        footerPanel.add(btnSave);
        footerPanel.add(btnCancel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void testConnection() {
        saveInMemory();
        lblStatus.setText("Status: Testing connection...");
        lblStatus.setForeground(UITheme.PRIMARY);
        lblStatus.paintImmediately(lblStatus.getVisibleRect());

        String err = DatabaseConnection.testConnection();
        if (err == null) {
            lblStatus.setText("Status: Connected successfully!");
            lblStatus.setForeground(UITheme.SUCCESS);
            DialogUtil.showSuccess(this, "Connection to MySQL database succeeded!", "Connected");
        } else {
            lblStatus.setText("Status: Connection failed");
            lblStatus.setForeground(UITheme.DANGER);
            DialogUtil.showError(this, "Connection error:\n" + err + "\n\nTip: If database 'smart_parking_db' does not exist yet, click 'Initialize Tables'.", "Connection Error");
        }
    }

    private void initializeTables() {
        saveInMemory();
        int opt = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "This will execute database/schema.sql on MySQL server at " + txtHost.getText() + ":" + txtPort.getText() + 
                "\nCreate database '" + txtDatabase.getText() + "' and tables if they do not exist.\nContinue?",
                "Initialize Database",
                javax.swing.JOptionPane.YES_NO_OPTION
        );
        if (opt != javax.swing.JOptionPane.YES_OPTION) return;

        try {
            DatabaseConnection.initializeDatabaseSchema();
            DialogUtil.showSuccess(this, "Database schema and seed data initialized successfully!", "Initialized");
            lblStatus.setText("Status: Database initialized and ready!");
            lblStatus.setForeground(UITheme.SUCCESS);
        } catch (SQLException | IOException ex) {
            DialogUtil.showError(this, "Failed to initialize database:\n" + ex.getMessage(), "Error");
            lblStatus.setText("Status: Initialization failed");
            lblStatus.setForeground(UITheme.DANGER);
        }
    }

    private void saveInMemory() {
        try {
            DatabaseConnection.saveConfiguration(
                    txtHost.getText(),
                    txtPort.getText(),
                    txtDatabase.getText(),
                    txtUsername.getText(),
                    new String(txtPassword.getPassword())
            );
        } catch (IOException ignored) {}
    }

    private void saveSettings() {
        try {
            DatabaseConnection.saveConfiguration(
                    txtHost.getText(),
                    txtPort.getText(),
                    txtDatabase.getText(),
                    txtUsername.getText(),
                    new String(txtPassword.getPassword())
            );
            DialogUtil.showSuccess(this, "Configuration saved successfully!", "Saved");
            dispose();
        } catch (IOException e) {
            DialogUtil.showError(this, "Failed to save configuration: " + e.getMessage(), "Error");
        }
    }
}
