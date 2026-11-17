package com.smartparking.ui.common;

import com.smartparking.util.UITheme;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Component;

/**
 * Utility for modern, consistent popups and message dialogs.
 */
public final class DialogUtil {

    static {
        UIManager.put("OptionPane.messageFont", UITheme.FONT_REGULAR);
        UIManager.put("OptionPane.buttonFont", UITheme.FONT_BOLD);
    }

    private DialogUtil() {
    }

    public static void showSuccess(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title != null ? title : "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title != null ? title : "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title != null ? title : "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title != null ? title : "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}
