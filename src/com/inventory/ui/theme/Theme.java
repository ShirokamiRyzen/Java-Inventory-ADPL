package com.inventory.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Theme {
    // Elegant Dark Theme Color Palette
    public static final Color PRIMARY = new Color(99, 102, 241);      // Indigo Accent
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229); // Darker Indigo
    public static final Color BG_DARK = new Color(15, 23, 42);         // Slate 900
    public static final Color BG_SIDEBAR = new Color(30, 41, 59);      // Slate 800
    public static final Color BG_CARD = new Color(30, 41, 59);         // Slate 800
    public static final Color BG_CARD_ALT = new Color(51, 65, 85);     // Slate 700
    
    public static final Color FG_LIGHT = new Color(248, 250, 252);     // Slate 50
    public static final Color FG_MUTED = new Color(148, 163, 184);     // Slate 400
    
    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald 500
    public static final Color WARNING = new Color(245, 158, 11);       // Amber 500
    public static final Color DANGER = new Color(239, 68, 68);         // Rose 500

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);

    public static void setupFlatLaf() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            // Customize FlatLaf defaults
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("TableHeader.background", BG_SIDEBAR);
            UIManager.put("TableHeader.foreground", FG_LIGHT);
            UIManager.put("Table.selectionBackground", PRIMARY);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.gridColor", BG_DARK);
            UIManager.put("ScrollBar.track", BG_DARK);
            UIManager.put("ScrollBar.thumb", BG_CARD_ALT);
            UIManager.put("ScrollBar.arc", 8);
            
            // Set default font
            UIManager.put("defaultFont", FONT_BODY);
            
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
        }
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_SIDEBAR);
        table.setForeground(FG_LIGHT);
        table.setFont(FONT_BODY);
        table.setRowHeight(35);
        table.setGridColor(BG_DARK);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SIDEBAR);
        header.setForeground(FG_LIGHT);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(100, 40));
        
        // Padded table cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10)); // left/right padding
                if (isSelected) {
                    c.setBackground(PRIMARY);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? BG_SIDEBAR : new Color(38, 50, 71));
                    c.setForeground(FG_LIGHT);
                }
                return c;
            }
        });
    }

    public static void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(FONT_HEADER);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add smooth hover behavior if possible or let FlatLaf handle standard state changes
    }

    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(new Color(0, 0, 0, 0));
        return card;
    }
}
