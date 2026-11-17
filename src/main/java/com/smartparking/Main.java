package com.smartparking;

import com.smartparking.ui.auth.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application Entry Point for Smart Parking Management System.
 * Operates in Standalone mode with Zero external Database requirement.
 */
public class Main {

    public static void main(String[] args) {
        // Enable high-DPI text antialiasing for modern Swing rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set native system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        printStartupBanner();

        // Launch Application GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    private static void printStartupBanner() {
        System.out.println("==================================================================");
        System.out.println("      🅿 SMART PARKING MANAGEMENT & BILLING SYSTEM v1.0.0        ");
        System.out.println("            Enterprise Desktop Software Solution                  ");
        System.out.println("==================================================================");
        System.out.println(" Java Version   : " + System.getProperty("java.version"));
        System.out.println(" Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        System.out.println(" Storage Engine : Embedded Standalone (Zero Database Setup Needed)");
        System.out.println(" Default Admin  : admin / admin123");
        System.out.println(" Status         : Ready & Operational");
        System.out.println("==================================================================");
    }
}
