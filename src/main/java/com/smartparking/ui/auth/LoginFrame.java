package com.smartparking.ui.auth;

import com.smartparking.model.Admin;
import com.smartparking.service.AuthServiceImpl;
import com.smartparking.service.IAuthService;
import com.smartparking.ui.MainFrame;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern, secure Administrator Login Screen.
 * Runs in standalone embedded mode - zero database installation required.
 */
public class LoginFrame extends JFrame {

    private final IAuthService authService = new AuthServiceImpl();
    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JCheckBox chkShowPassword = new JCheckBox("Show Password");
    private final JLabel lblError = new JLabel(" ");

    public LoginFrame() {
        super("Smart Parking Management System - Admin Login");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Top Brand Header Banner
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setBackground(UITheme.SIDEBAR_BG);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));

        JLabel lblLogo = new JLabel("🅿 SMART PARKING", SwingConstants.CENTER);
        lblLogo.setFont(UITheme.FONT_TITLE);
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Management & Billing System (Standalone Edition)", SwingConstants.CENTER);
        lblSub.setFont(UITheme.FONT_REGULAR);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        brandPanel.add(lblLogo, BorderLayout.CENTER);
        brandPanel.add(lblSub, BorderLayout.SOUTH);
        add(brandPanel, BorderLayout.NORTH);

        // Main Login Card
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        centerWrapper.setOpaque(false);

        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(390, 320));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Card Title
        JLabel lblLoginTitle = new JLabel("Administrator Sign In");
        lblLoginTitle.setFont(UITheme.FONT_HEADER);
        lblLoginTitle.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 0;
        card.add(lblLoginTitle, gbc);

        // Error message banner
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.DANGER);
        gbc.gridy = 1;
        card.add(lblError, gbc);

        // Username Label & Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_BOLD);
        lblUser.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 2;
        card.add(lblUser, gbc);

        txtUsername.setText("admin"); // default convenience
        txtUsername.setBorder(UITheme.BORDER_INPUT);
        txtUsername.setFont(UITheme.FONT_REGULAR);
        gbc.gridy = 3;
        card.add(txtUsername, gbc);

        // Password Label & Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BOLD);
        lblPass.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 4;
        card.add(lblPass, gbc);

        txtPassword.setText("admin123"); // default initial seed
        txtPassword.setBorder(UITheme.BORDER_INPUT);
        txtPassword.setFont(UITheme.FONT_REGULAR);
        gbc.gridy = 5;
        card.add(txtPassword, gbc);

        // Show password checkbox
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setForeground(UITheme.TEXT_MUTED);
        chkShowPassword.setOpaque(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        gbc.gridy = 6;
        card.add(chkShowPassword, gbc);

        // Sign In Button
        ModernButton btnLogin = new ModernButton("Sign In to Dashboard", ModernButton.Style.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(340, 42));
        btnLogin.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(12, 5, 5, 5);
        card.add(btnLogin, gbc);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer Mode Indicator
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UITheme.CONTENT_BG);

        JLabel lblMode = new JLabel("● Storage: Embedded Standalone (Zero Configuration Required)");
        lblMode.setFont(UITheme.FONT_SMALL);
        lblMode.setForeground(UITheme.SUCCESS);
        footerPanel.add(lblMode);
        add(footerPanel, BorderLayout.SOUTH);

        // Enter key listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void performLogin() {
        lblError.setText(" ");
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        try {
            Admin admin = authService.login(username, password);
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(admin);
                mainFrame.setVisible(true);
            });
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
        }
    }
}
