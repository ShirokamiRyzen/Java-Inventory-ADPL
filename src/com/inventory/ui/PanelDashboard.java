package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.BarangMasukDAO;
import com.inventory.dao.BarangKeluarDAO;
import com.inventory.dao.PengajuanPembelianDAO;
import com.inventory.model.Barang;
import com.inventory.ui.components.CardDashboard;
import com.inventory.ui.components.StockChartComponent;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelDashboard extends JPanel {
    private CardDashboard cardTotalBarang;
    private CardDashboard cardTotalStok;
    private CardDashboard cardPendingPengajuan;
    private CardDashboard cardBarangKeluar;
    
    private StockChartComponent stockChart;
    private JTable tblLowStock;
    private DefaultTableModel lowStockModel;

    private BarangDAO barangDAO;
    private BarangMasukDAO masukDAO;
    private BarangKeluarDAO keluarDAO;
    private PengajuanPembelianDAO pengajuanDAO;

    public PanelDashboard() {
        barangDAO = new BarangDAO();
        masukDAO = new BarangMasukDAO();
        keluarDAO = new BarangKeluarDAO();
        pengajuanDAO = new PengajuanPembelianDAO();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initUI();
        refreshData();
    }

    private void initUI() {
        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Dashboard Analitik");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER SCROLL PANEL (To fit everything nicely) ---
        JPanel mainContent = new JPanel();
        mainContent.setOpaque(false);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

        // Row 1: Metric Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        cardTotalBarang = new CardDashboard("Total Barang", "0", Theme.PRIMARY, "box");
        cardTotalStok = new CardDashboard("Total Stok", "0", Theme.SUCCESS, "in");
        cardPendingPengajuan = new CardDashboard("Pending Pengajuan", "0", Theme.WARNING, "clock");
        cardBarangKeluar = new CardDashboard("Barang Keluar", "0", Theme.DANGER, "out");

        metricsPanel.add(cardTotalBarang);
        metricsPanel.add(cardTotalStok);
        metricsPanel.add(cardPendingPengajuan);
        metricsPanel.add(cardBarangKeluar);

        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(metricsPanel);

        // Row 2: Chart & Low Stock alerts side-by-side
        JPanel row2Panel = new JPanel(new GridBagLayout());
        row2Panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 0, 0, 0);

        // Chart container (60% width)
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        stockChart = new StockChartComponent();
        row2Panel.add(stockChart, gbc);

        // Low stock container (40% width)
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(20, 15, 0, 0);
        
        JPanel lowStockCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        lowStockCard.setOpaque(false);
        lowStockCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblLowStockTitle = new JLabel("Peringatan Stok Menipis (< 10)");
        lblLowStockTitle.setFont(Theme.FONT_SUBTITLE);
        lblLowStockTitle.setForeground(Theme.DANGER);
        lblLowStockTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        lowStockCard.add(lblLowStockTitle, BorderLayout.NORTH);

        // Table setup for low stock
        String[] columns = {"Kode", "Nama Barang", "Kategori", "Stok"};
        lowStockModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLowStock = new JTable(lowStockModel);
        Theme.styleTable(tblLowStock);
        
        JScrollPane scrollTable = new JScrollPane(tblLowStock);
        scrollTable.getViewport().setBackground(Theme.BG_SIDEBAR);
        scrollTable.setBorder(BorderFactory.createLineBorder(Theme.BG_DARK, 1));
        lowStockCard.add(scrollTable, BorderLayout.CENTER);
        
        row2Panel.add(lowStockCard, gbc);

        mainContent.add(row2Panel);
        
        // Wrap in a ScrollPane just in case the window size is small
        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        add(mainScroll, BorderLayout.CENTER);
    }

    public void refreshData() {
        // Load data in background to keep UI snappy
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private int totalBarang;
            private int totalStok;
            private int pendingReq;
            private int totalKeluarCount;
            private List<Barang> allBarang;
            private List<Barang> lowStockBarang;

            @Override
            protected Void doInBackground() {
                allBarang = barangDAO.getAllBarang();
                totalBarang = allBarang.size();
                totalStok = allBarang.stream().mapToInt(Barang::getStok).sum();
                
                lowStockBarang = allBarang.stream()
                        .filter(b -> b.getStok() < 10)
                        .collect(Collectors.toList());

                pendingReq = (int) pengajuanDAO.getAllPengajuan().stream()
                        .filter(p -> "Pending".equalsIgnoreCase(p.getStatusPengajuan()))
                        .count();

                totalKeluarCount = keluarDAO.getAllBarangKeluar().stream()
                        .mapToInt(bk -> bk.getJumlahKeluar())
                        .sum();

                return null;
            }

            @Override
            protected void done() {
                // Update elements on Event Dispatch Thread (EDT)
                cardTotalBarang.setValue(String.valueOf(totalBarang));
                cardTotalStok.setValue(String.valueOf(totalStok));
                cardPendingPengajuan.setValue(String.valueOf(pendingReq));
                cardBarangKeluar.setValue(String.valueOf(totalKeluarCount));

                stockChart.setData(allBarang);

                // Populate low stock table
                lowStockModel.setRowCount(0);
                for (Barang b : lowStockBarang) {
                    lowStockModel.addRow(new Object[]{
                        b.getIdBarang(),
                        b.getNamaBarang(),
                        b.getKategori(),
                        b.getStok()
                    });
                }
            }
        };
        worker.execute();
    }
}
