package com.inventory.ui.components;

import com.inventory.ui.theme.Theme;
import com.inventory.model.Barang;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class StockChartComponent extends JComponent {
    private List<Barang> items = new ArrayList<>();

    public StockChartComponent() {
        setPreferredSize(new Dimension(450, 250));
    }

    public void setData(List<Barang> items) {
        // Take top 6 items to display in the chart
        this.items = items.subList(0, Math.min(items.size(), 6));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background card
        g2.setColor(Theme.BG_CARD);
        g2.fillRoundRect(0, 0, width, height, 16, 16);

        // Header Title
        g2.setColor(Theme.FG_LIGHT);
        g2.setFont(Theme.FONT_SUBTITLE);
        g2.drawString("Visualisasi Stok Barang", 20, 30);

        if (items.isEmpty()) {
            g2.setColor(Theme.FG_MUTED);
            g2.setFont(Theme.FONT_BODY);
            g2.drawString("Tidak ada data barang untuk ditampilkan", width / 2 - 120, height / 2);
            g2.dispose();
            return;
        }

        // Draw Chart Axes / Bars
        int chartYStart = 60;
        int chartYEnd = height - 45;
        int chartHeight = chartYEnd - chartYStart;
        int maxStock = 1;

        // Find max stock for scaling
        for (Barang item : items) {
            if (item.getStok() > maxStock) {
                maxStock = item.getStok();
            }
        }

        int barCount = items.size();
        int availableWidth = width - 40;
        int gap = 20;
        int barWidth = (availableWidth - (gap * (barCount + 1))) / barCount;

        for (int i = 0; i < barCount; i++) {
            Barang item = items.get(i);
            int barHeight = (int) (((double) item.getStok() / maxStock) * (chartHeight - 30));
            if (barHeight < 5) barHeight = 5; // minimum height to be visible

            int barX = 20 + gap + (i * (barWidth + gap));
            int barY = chartYEnd - barHeight;

            // Draw shadow or glow effect
            g2.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 30));
            g2.fillRoundRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4, 8, 8);

            // Draw main bar
            g2.setColor(Theme.PRIMARY);
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);

            // Draw value on top of bar
            g2.setColor(Theme.FG_LIGHT);
            g2.setFont(Theme.FONT_SMALL_BOLD);
            String stockStr = String.valueOf(item.getStok());
            FontMetrics fmVal = g2.getFontMetrics();
            int valX = barX + (barWidth - fmVal.stringWidth(stockStr)) / 2;
            g2.drawString(stockStr, valX, barY - 8);

            // Draw label below bar (truncated if too long)
            g2.setColor(Theme.FG_MUTED);
            g2.setFont(Theme.FONT_SMALL);
            String name = item.getNamaBarang();
            if (name.length() > 10) {
                name = name.substring(0, 8) + "..";
            }
            FontMetrics fmLabel = g2.getFontMetrics();
            int labelX = barX + (barWidth - fmLabel.stringWidth(name)) / 2;
            g2.drawString(name, labelX, chartYEnd + 20);
        }

        g2.dispose();
    }
}
