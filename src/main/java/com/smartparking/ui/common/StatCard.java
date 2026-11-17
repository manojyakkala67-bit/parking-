package com.smartparking.ui.common;

import com.smartparking.util.UITheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/**
 * Modern KPI Statistics Card widget.
 */
public class StatCard extends CardPanel {

    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtitle;
    private final Color accentColor;
    private final String iconSymbol;

    public StatCard(String title, String initialValue, String subtitle, Color accentColor, String iconSymbol) {
        super(14);
        this.accentColor = accentColor;
        this.iconSymbol = iconSymbol != null ? iconSymbol : "●";
        setLayout(new BorderLayout(10, 5));
        setPreferredSize(new Dimension(210, 105));

        // Left Accent Bar / Icon Badge
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded icon background
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fillRoundRect(10, 15, 45, 45, 12, 12);

                // Symbol text
                g2.setColor(accentColor);
                g2.setFont(UITheme.FONT_TITLE);
                g2.drawString(iconSymbol, 22, 45);

                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(65, 80));

        // Text Info Panel
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SMALL);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        lblValue = new JLabel(initialValue);
        lblValue.setFont(UITheme.FONT_KPI_VAL);
        lblValue.setForeground(UITheme.TEXT_DARK);

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        textPanel.add(lblTitle);
        textPanel.add(lblValue);
        textPanel.add(lblSubtitle);

        add(iconPanel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }
}
