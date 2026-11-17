package com.smartparking.ui.common;

import com.smartparking.util.UITheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Custom Donut Chart component for displaying parking occupancy.
 */
public class CustomDonutChart extends JPanel {

    private int available = 0;
    private int occupied = 0;
    private int maintenance = 0;

    public CustomDonutChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(280, 240));
    }

    public void updateData(int available, int occupied, int maintenance) {
        this.available = Math.max(0, available);
        this.occupied = Math.max(0, occupied);
        this.maintenance = Math.max(0, maintenance);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int total = available + occupied + maintenance;
        int size = Math.min(getWidth(), getHeight() - 50);
        int x = (getWidth() - size) / 2;
        int y = 10;
        int holeSize = (int) (size * 0.60);
        int holeX = x + (size - holeSize) / 2;
        int holeY = y + (size - holeSize) / 2;

        if (total == 0) {
            g2.setColor(new Color(226, 232, 240));
            g2.fillOval(x, y, size, size);
            g2.setColor(Color.WHITE);
            g2.fillOval(holeX, holeY, holeSize, holeSize);

            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(UITheme.FONT_REGULAR);
            drawCenteredString(g2, "No Slots", holeX, holeY, holeSize, holeSize);
        } else {
            // Calculate angles
            double occupiedAngle = (occupied * 360.0) / total;
            double availableAngle = (available * 360.0) / total;
            double maintenanceAngle = 360.0 - occupiedAngle - availableAngle;

            int startAngle = 90;

            // Occupied slice (Red)
            if (occupied > 0) {
                g2.setColor(UITheme.DANGER);
                g2.fillArc(x, y, size, size, startAngle, -(int) Math.round(occupiedAngle));
                startAngle -= (int) Math.round(occupiedAngle);
            }

            // Available slice (Green)
            if (available > 0) {
                g2.setColor(UITheme.SUCCESS);
                g2.fillArc(x, y, size, size, startAngle, -(int) Math.round(availableAngle));
                startAngle -= (int) Math.round(availableAngle);
            }

            // Maintenance slice (Amber)
            if (maintenance > 0) {
                g2.setColor(UITheme.WARNING);
                g2.fillArc(x, y, size, size, startAngle, -(int) Math.round(maintenanceAngle));
            }

            // Inner circle (Donut hole)
            g2.setColor(Color.WHITE);
            g2.fillOval(holeX, holeY, holeSize, holeSize);

            // Center Stats
            int pct = (int) Math.round((occupied * 100.0) / total);
            g2.setColor(UITheme.TEXT_DARK);
            g2.setFont(UITheme.FONT_KPI_VAL);
            drawCenteredString(g2, pct + "%", holeX, holeY - 10, holeSize, holeSize);

            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(UITheme.FONT_SMALL);
            drawCenteredString(g2, "Occupied", holeX, holeY + 18, holeSize, holeSize);
        }

        // Draw Legend at the bottom
        int legendY = y + size + 15;
        drawLegendItem(g2, 20, legendY, UITheme.SUCCESS, "Available: " + available);
        drawLegendItem(g2, 110, legendY, UITheme.DANGER, "Occupied: " + occupied);
        drawLegendItem(g2, 205, legendY, UITheme.WARNING, "Maint: " + maintenance);

        g2.dispose();
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, 10, 10, 4, 4);
        g2.setColor(UITheme.TEXT_SECONDARY);
        g2.setFont(UITheme.FONT_SMALL);
        g2.drawString(label, x + 14, y + 9);
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);
    }
}
