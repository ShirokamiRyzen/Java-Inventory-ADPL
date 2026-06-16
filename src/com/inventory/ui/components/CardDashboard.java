package com.inventory.ui.components;

import com.inventory.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class CardDashboard extends JPanel {
    private String title;
    private String value;
    private Color accentColor;
    private String iconName;

    public CardDashboard(String title, String value, Color accentColor, String iconName) {
        this.title = title;
        this.value = value;
        this.accentColor = accentColor;
        this.iconName = iconName;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(220, 110));
    }

    public void setValue(String value) {
        this.value = value;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw card background
        g2.setColor(Theme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        
        // Draw left accent border
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 8, getHeight(), 8, 8);
        g2.fillRect(4, 0, 4, getHeight()); // smooth out right side of the round accent

        // Draw title
        g2.setColor(Theme.FG_MUTED);
        g2.setFont(Theme.FONT_SMALL_BOLD);
        g2.drawString(title.toUpperCase(), 20, 30);
        
        // Draw value
        g2.setColor(Theme.FG_LIGHT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.drawString(value, 20, 70);

        // Draw decorative vector icon depending on type
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
        int iconSize = 48;
        int iconX = getWidth() - iconSize - 20;
        int iconY = (getHeight() - iconSize) / 2;
        g2.fillOval(iconX, iconY, iconSize, iconSize);

        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(2.5f));
        if ("box".equalsIgnoreCase(iconName)) {
            // Draw a box icon
            g2.drawRect(iconX + 15, iconY + 18, 18, 15);
            g2.drawLine(iconX + 15, iconY + 18, iconX + 24, iconY + 12);
            g2.drawLine(iconX + 33, iconY + 18, iconX + 24, iconY + 12);
        } else if ("in".equalsIgnoreCase(iconName)) {
            // Draw incoming arrow / basket icon
            g2.drawArc(iconX + 14, iconY + 20, 20, 15, 180, 180);
            g2.drawLine(iconX + 24, iconY + 10, iconX + 24, iconY + 25);
            g2.drawLine(iconX + 20, iconY + 21, iconX + 24, iconY + 25);
            g2.drawLine(iconX + 28, iconY + 21, iconX + 24, iconY + 25);
        } else if ("out".equalsIgnoreCase(iconName)) {
            // Draw outgoing arrow / basket icon
            g2.drawArc(iconX + 14, iconY + 20, 20, 15, 180, 180);
            g2.drawLine(iconX + 24, iconY + 25, iconX + 24, iconY + 10);
            g2.drawLine(iconX + 20, iconY + 14, iconX + 24, iconY + 10);
            g2.drawLine(iconX + 28, iconY + 14, iconX + 24, iconY + 10);
        } else if ("clock".equalsIgnoreCase(iconName)) {
            // Draw clock icon
            g2.drawOval(iconX + 14, iconY + 14, 20, 20);
            g2.drawLine(iconX + 24, iconY + 24, iconX + 24, iconY + 18);
            g2.drawLine(iconX + 24, iconY + 24, iconX + 29, iconY + 24);
        } else {
            // Default user icon
            g2.drawOval(iconX + 19, iconY + 14, 10, 10);
            g2.drawArc(iconX + 14, iconY + 25, 20, 10, 0, 180);
        }

        g2.dispose();
    }
}
