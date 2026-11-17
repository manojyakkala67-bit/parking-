package com.smartparking.ui.common;

import com.smartparking.model.VehicleType;
import com.smartparking.util.UITheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;

/**
 * Custom Bar Chart displaying vehicle category distribution.
 */
public class CustomBarChart extends JPanel {

    private Map<VehicleType, Integer> data = new EnumMap<>(VehicleType.class);

    public CustomBarChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 240));
        for (VehicleType vt : VehicleType.values()) {
            data.put(vt, 0);
        }
    }

    public void updateData(Map<VehicleType, Integer> countMap) {
        if (countMap != null) {
            this.data = new EnumMap<>(countMap);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxVal = 1;
        for (int v : data.values()) {
            if (v > maxVal) maxVal = v;
        }

        int padding = 30;
        int barHeight = 28;
        int spacing = 22;
        int startY = 30;
        int maxBarWidth = getWidth() - 140;

        VehicleType[] types = { VehicleType.BIKE, VehicleType.CAR, VehicleType.TRUCK };
        Color[] colors = { UITheme.PRIMARY, UITheme.SUCCESS, UITheme.WARNING };

        FontMetrics fm = g2.getFontMetrics(UITheme.FONT_BOLD);

        for (int i = 0; i < types.length; i++) {
            VehicleType vt = types[i];
            int count = data.getOrDefault(vt, 0);
            int barY = startY + i * (barHeight + spacing);

            // Label
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.setFont(UITheme.FONT_BOLD);
            g2.drawString(vt.getDisplayName(), padding, barY + 18);

            // Background Bar
            int barX = padding + 65;
            g2.setColor(new Color(241, 245, 249));
            g2.fillRoundRect(barX, barY, maxBarWidth, barHeight, 8, 8);

            // Active Value Bar
            int currentBarWidth = (int) Math.round(((double) count / maxVal) * maxBarWidth);
            if (currentBarWidth < 6 && count > 0) currentBarWidth = 6;

            if (currentBarWidth > 0) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(barX, barY, currentBarWidth, barHeight, 8, 8);
            }

            // Value text
            g2.setColor(UITheme.TEXT_DARK);
            g2.setFont(UITheme.FONT_SMALL);
            String valStr = count + " vehicles";
            g2.drawString(valStr, barX + maxBarWidth + 10, barY + 18);
        }

        g2.dispose();
    }
}
