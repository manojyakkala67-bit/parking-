package com.smartparking.util;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/**
 * Centralized theme constants and UI styling helpers for the Swing application.
 */
public final class UITheme {

    // Color Palette
    public static final Color SIDEBAR_BG = new Color(0x1E, 0x29, 0x3B);       // Dark Navy Slate
    public static final Color SIDEBAR_ACTIVE = new Color(0x33, 0x41, 0x55);   // Lighter Navy
    public static final Color SIDEBAR_TEXT = new Color(0xE2, 0xE8, 0xF0);     // Light Slate
    public static final Color SIDEBAR_MUTED = new Color(0x94, 0xA3, 0xB8);    // Muted Slate

    public static final Color CONTENT_BG = new Color(0xF8, 0xFA, 0xFC);       // Soft Gray Background
    public static final Color CARD_BG = Color.WHITE;                          // Crisp White Card
    public static final Color CARD_BORDER = new Color(0xE2, 0xE8, 0xF0);      // Subtle Border

    public static final Color PRIMARY = new Color(0x25, 0x63, 0xEB);          // Royal Blue
    public static final Color PRIMARY_HOVER = new Color(0x1D, 0x4E, 0xD8);    // Darker Blue
    public static final Color PRIMARY_LIGHT = new Color(0xDB, 0xEA, 0xFE);    // Light Blue Tint

    public static final Color SUCCESS = new Color(0x10, 0xB9, 0x81);          // Emerald Green
    public static final Color SUCCESS_HOVER = new Color(0x05, 0x96, 0x69);
    public static final Color SUCCESS_LIGHT = new Color(0xD1, 0xFA, 0xE5);

    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);           // Crimson Red
    public static final Color DANGER_HOVER = new Color(0xDC, 0x26, 0x26);
    public static final Color DANGER_LIGHT = new Color(0xFE, 0xE2, 0xE2);

    public static final Color WARNING = new Color(0xF5, 0x9E, 0x0B);          // Amber Orange
    public static final Color WARNING_HOVER = new Color(0xD9, 0x77, 0x06);
    public static final Color WARNING_LIGHT = new Color(0xFE, 0xFD, 0xE8);

    public static final Color TEXT_DARK = new Color(0x0F, 0x17, 0x2A);        // Slate 900
    public static final Color TEXT_SECONDARY = new Color(0x47, 0x55, 0x69);   // Slate 600
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);       // Slate 400

    public static final Color TABLE_HEADER_BG = new Color(0xF1, 0xF5, 0xF9);
    public static final Color TABLE_ROW_ALT = new Color(0xF8, 0xFA, 0xFC);
    public static final Color TABLE_SELECTION = new Color(0xBF, 0xDB, 0xFE);

    // Modern Fonts (Segoe UI on Windows, with fallback)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_KPI_VAL = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    // Common Borders
    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );

    private UITheme() {
    }
}
