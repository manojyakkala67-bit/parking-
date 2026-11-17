package com.smartparking.ui.common;

import com.smartparking.util.UITheme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern custom styled JButton with rounded corners and hover effects.
 */
public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY, OUTLINE
    }

    private Color normalBg;
    private Color hoverBg;
    private Color pressedBg;
    private Color textColor;
    private int cornerRadius = 10;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private final Style style;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setupStyle();
        init();
    }

    private void setupStyle() {
        switch (style) {
            case SUCCESS -> {
                normalBg = UITheme.SUCCESS;
                hoverBg = UITheme.SUCCESS_HOVER;
                pressedBg = UITheme.SUCCESS_HOVER.darker();
                textColor = Color.WHITE;
            }
            case DANGER -> {
                normalBg = UITheme.DANGER;
                hoverBg = UITheme.DANGER_HOVER;
                pressedBg = UITheme.DANGER_HOVER.darker();
                textColor = Color.WHITE;
            }
            case WARNING -> {
                normalBg = UITheme.WARNING;
                hoverBg = UITheme.WARNING_HOVER;
                pressedBg = UITheme.WARNING_HOVER.darker();
                textColor = Color.WHITE;
            }
            case SECONDARY -> {
                normalBg = UITheme.SIDEBAR_ACTIVE;
                hoverBg = UITheme.SIDEBAR_BG;
                pressedBg = UITheme.SIDEBAR_BG.darker();
                textColor = Color.WHITE;
            }
            case OUTLINE -> {
                normalBg = Color.WHITE;
                hoverBg = UITheme.PRIMARY_LIGHT;
                pressedBg = UITheme.PRIMARY_LIGHT.darker();
                textColor = UITheme.PRIMARY;
            }
            case PRIMARY -> {
                normalBg = UITheme.PRIMARY;
                hoverBg = UITheme.PRIMARY_HOVER;
                pressedBg = UITheme.PRIMARY_HOVER.darker();
                textColor = Color.WHITE;
            }
        }
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(textColor);
        setFont(UITheme.FONT_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, 38));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(203, 213, 225));
        } else if (isPressed) {
            g2.setColor(pressedBg);
        } else if (isHovered) {
            g2.setColor(hoverBg);
        } else {
            g2.setColor(normalBg);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        if (style == Style.OUTLINE) {
            g2.setColor(UITheme.PRIMARY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
