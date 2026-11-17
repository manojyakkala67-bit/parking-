package com.smartparking.ui.settings;

import com.smartparking.service.AuthServiceImpl;
import com.smartparking.service.IAuthService;
import com.smartparking.ui.common.CardPanel;
import com.smartparking.ui.common.DialogUtil;
import com.smartparking.ui.common.ModernButton;
import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Dialog for updating the administrator password.
 */
public class ChangePasswordDialog extends JDialog {

    private final IAuthService authService = new AuthServiceImpl();
    private final JPasswordField txtOldPass = new JPasswordField(15);
    private final JPasswordField txtNewPass = new JPasswordField(15);
    private final JPasswordField txtConfirmPass = new JPasswordField(15);

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Admin Password", true);
        initUI();
    }

    private void initUI() {
        setSize(440, 340);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UITheme.CONTENT_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SIDEBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Change Password");
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Update your administrator account password");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.SIDEBAR_MUTED);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        CardPanel card = new CardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtOldPass.setBorder(UITheme.BORDER_INPUT);
        txtNewPass.setBorder(UITheme.BORDER_INPUT);
        txtConfirmPass.setBorder(UITheme.BORDER_INPUT);

        addRow(card, gbc, 0, "Current Password:", txtOldPass);
        addRow(card, gbc, 1, "New Password:", txtNewPass);
        addRow(card, gbc, 2, "Confirm Password:", txtConfirmPass);

        add(card, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(UITheme.CONTENT_BG);

        ModernButton btnSave = new ModernButton("Update Password", ModernButton.Style.PRIMARY);
        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.Style.SECONDARY);

        btnSave.addActionListener(e -> handleChangePassword());
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnSave);
        footer.add(btnCancel);
        add(footer, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JPasswordField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(field, gbc);
    }

    private void handleChangePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            DialogUtil.showWarning(this, "Please enter both old and new passwords.", "Validation");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            DialogUtil.showWarning(this, "New password and confirmation do not match.", "Validation");
            return;
        }

        if (authService.getCurrentAdmin() == null) {
            DialogUtil.showError(this, "No active admin session found.", "Error");
            return;
        }

        try {
            boolean ok = authService.changePassword(authService.getCurrentAdmin().getAdminId(), oldPass, newPass);
            if (ok) {
                DialogUtil.showSuccess(this, "Password updated successfully!", "Success");
                dispose();
            } else {
                DialogUtil.showError(this, "Could not update password.", "Error");
            }
        } catch (Exception ex) {
            DialogUtil.showError(this, ex.getMessage(), "Error");
        }
    }
}
