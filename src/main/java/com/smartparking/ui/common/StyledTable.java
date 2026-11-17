package com.smartparking.ui.common;

import com.smartparking.util.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Modern styled JTable with alternating row colors, badges, and clean headers.
 */
public class StyledTable extends JTable {

    public StyledTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(36);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(UITheme.CARD_BORDER);
        setSelectionBackground(UITheme.TABLE_SELECTION);
        setSelectionForeground(UITheme.TEXT_DARK);
        setFont(UITheme.FONT_REGULAR);
        getTableHeader().setReorderingAllowed(false);

        // Header Styling
        getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                label.setText(value != null ? value.toString().toUpperCase() : "");
                label.setFont(UITheme.FONT_BADGE);
                label.setForeground(UITheme.TEXT_SECONDARY);
                label.setBackground(UITheme.TABLE_HEADER_BG);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.CARD_BORDER),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                return label;
            }
        });
        getTableHeader().setPreferredSize(new Dimension(100, 42));

        // Default Cell Renderer with alternating colors
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                           boolean isSelected, boolean hasFocus, 
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ROW_ALT);
                    c.setForeground(UITheme.TEXT_DARK);
                }

                // Check for Status or Type badges
                if (value != null) {
                    String str = value.toString();
                    if (str.equals("AVAILABLE") || str.equals("SUCCESS") || str.equals("COMPLETED")) {
                        return createBadgeComponent(str, UITheme.SUCCESS_LIGHT, UITheme.SUCCESS);
                    } else if (str.equals("OCCUPIED") || str.equals("FAILED")) {
                        return createBadgeComponent(str, UITheme.DANGER_LIGHT, UITheme.DANGER);
                    } else if (str.equals("MAINTENANCE") || str.equals("PENDING") || str.equals("ACTIVE")) {
                        return createBadgeComponent(str, UITheme.WARNING_LIGHT, UITheme.WARNING);
                    } else if (str.equals("BIKE") || str.equals("CAR") || str.equals("TRUCK")) {
                        return createBadgeComponent(str, UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
                    }
                }

                return c;
            }
        });
    }

    private Component createBadgeComponent(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(2, 6, getWidth() - 4, getHeight() - 12, 10, 10);
                g2.setColor(fg);
                g2.drawRoundRect(2, 6, getWidth() - 4, getHeight() - 12, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(UITheme.FONT_BADGE);
        badge.setForeground(fg);
        badge.setOpaque(false);
        return badge;
    }
}
