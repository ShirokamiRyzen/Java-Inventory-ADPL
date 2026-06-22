package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.BarangMasukDAO;
import com.inventory.dao.BarangKeluarDAO;
import com.inventory.dao.LaporanDAO;
import com.inventory.model.Barang;
import com.inventory.model.BarangMasuk;
import com.inventory.model.BarangKeluar;
import com.inventory.model.Laporan;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;
import com.inventory.ui.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PanelLaporan extends JPanel {
    private JComboBox<String> cbJenisLaporan;
    private DatePicker dpTanggalMulai;
    private DatePicker dpTanggalSelesai;
    private JTextArea txtPreview;
    private JButton btnGenerate, btnSaveReport, btnExportExcel;
    private JTable tblHistory;
    private DefaultTableModel historyModel;

    private LaporanDAO laporanDAO;
    private BarangDAO barangDAO;
    private BarangMasukDAO masukDAO;
    private BarangKeluarDAO keluarDAO;
    private User currentUser;
    
    private String currentReportContent = "";

    public PanelLaporan(User user) {
        this.currentUser = user;
        this.laporanDAO = new LaporanDAO();
        this.barangDAO = new BarangDAO();
        this.masukDAO = new BarangMasukDAO();
        this.keluarDAO = new BarangKeluarDAO();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initUI();
        loadHistory();
    }

    private void initUI() {
        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Laporan & Audit");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER CONTENT ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 0);

        // Control Selector Card
        JPanel ctrlCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        ctrlCard.setOpaque(false);
        
        JLabel lblSelect = new JLabel("Laporan:");
        lblSelect.setFont(Theme.FONT_HEADER);
        lblSelect.setForeground(Theme.FG_LIGHT);
        ctrlCard.add(lblSelect);

        String[] reportTypes = {"Stok Barang", "Barang Masuk", "Barang Keluar"};
        cbJenisLaporan = new JComboBox<>(reportTypes);
        cbJenisLaporan.setBackground(Theme.BG_DARK);
        cbJenisLaporan.setForeground(Theme.FG_LIGHT);
        cbJenisLaporan.setPreferredSize(new Dimension(150, 35));
        ctrlCard.add(cbJenisLaporan);

        // Date Period inputs
        JLabel lblMulai = new JLabel("Mulai:");
        lblMulai.setFont(Theme.FONT_HEADER);
        lblMulai.setForeground(Theme.FG_LIGHT);
        ctrlCard.add(lblMulai);

        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calStart.getTime();

        dpTanggalMulai = new DatePicker(startDate);
        ctrlCard.add(dpTanggalMulai);

        JLabel lblSelesai = new JLabel("Selesai:");
        lblSelesai.setFont(Theme.FONT_HEADER);
        lblSelesai.setForeground(Theme.FG_LIGHT);
        ctrlCard.add(lblSelesai);

        Date endDate = new Date();
        dpTanggalSelesai = new DatePicker(endDate);
        ctrlCard.add(dpTanggalSelesai);

        btnGenerate = new JButton("Tampilkan");
        Theme.styleButton(btnGenerate, Theme.PRIMARY, Color.WHITE);
        ctrlCard.add(btnGenerate);

        btnSaveReport = new JButton("Simpan Log");
        Theme.styleButton(btnSaveReport, Theme.SUCCESS, Color.WHITE);
        btnSaveReport.setEnabled(false);
        ctrlCard.add(btnSaveReport);



        btnExportExcel = new JButton("Ekspor Excel");
        Theme.styleButton(btnExportExcel, Theme.SUCCESS, Color.WHITE);
        btnExportExcel.setEnabled(false);
        ctrlCard.add(btnExportExcel);

        // Keep date pickers always enabled for user flexibility
        dpTanggalMulai.setEnabled(true);
        dpTanggalSelesai.setEnabled(true);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.05;
        mainPanel.add(ctrlCard, gbc);

        // Split Preview and History logs using a clean responsive GridLayout instead of JSplitPane
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        // Left: Print Preview Area
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setOpaque(false);
        
        JLabel lblPreview = new JLabel("Preview Laporan");
        lblPreview.setFont(Theme.FONT_SUBTITLE);
        lblPreview.setForeground(Theme.FG_LIGHT);
        lblPreview.setBorder(new EmptyBorder(0, 0, 5, 0));
        previewPanel.add(lblPreview, BorderLayout.NORTH);

        txtPreview = new JTextArea();
        txtPreview.setEditable(false);
        txtPreview.setBackground(Theme.BG_SIDEBAR);
        txtPreview.setForeground(new Color(192, 250, 192)); // Light green matrix terminal style text
        txtPreview.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtPreview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPreview = new JScrollPane(txtPreview);
        scrollPreview.setBorder(BorderFactory.createLineBorder(Theme.BG_DARK, 1));
        previewPanel.add(scrollPreview, BorderLayout.CENTER);

        splitPanel.add(previewPanel);

        // Right: Report Log History
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setOpaque(false);
        historyPanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel lblHistory = new JLabel("Audit Log Riwayat Laporan");
        lblHistory.setFont(Theme.FONT_SUBTITLE);
        lblHistory.setForeground(Theme.FG_LIGHT);
        lblHistory.setBorder(new EmptyBorder(0, 0, 5, 0));
        historyPanel.add(lblHistory, BorderLayout.NORTH);

        String[] cols = {"ID Log", "Tanggal Log", "Tipe Laporan", "Keterangan"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblHistory = new JTable(historyModel);
        Theme.styleTable(tblHistory);

        JScrollPane scrollHistory = new JScrollPane(tblHistory);
        scrollHistory.getViewport().setBackground(Theme.BG_SIDEBAR);
        scrollHistory.setBorder(BorderFactory.createLineBorder(Theme.BG_DARK, 1));
        historyPanel.add(scrollHistory, BorderLayout.CENTER);

        splitPanel.add(historyPanel);

        gbc.gridy = 1; gbc.weighty = 0.95;
        mainPanel.add(splitPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Listeners
        btnGenerate.addActionListener(e -> generateReport());
        btnSaveReport.addActionListener(e -> saveReportToLog());
        btnExportExcel.addActionListener(e -> exportToExcel());
    }

    private void loadHistory() {
        SwingWorker<List<Laporan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Laporan> doInBackground() {
                return laporanDAO.getAllLaporan();
            }

            @Override
            protected void done() {
                try {
                    List<Laporan> list = get();
                    historyModel.setRowCount(0);
                    for (Laporan l : list) {
                        historyModel.addRow(new Object[]{
                            l.getIdLaporan(),
                            l.getTanggalLaporan(),
                            l.getJenisLaporan(),
                            l.getKeterangan()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void generateReport() {
        int selectedType = cbJenisLaporan.getSelectedIndex();
        Date dateMulai = dpTanggalMulai.getSelectedDate();
        Date dateSelesai = dpTanggalSelesai.getSelectedDate();

        // Validasi periode tanggal untuk seluruh tipe laporan
        if (dateMulai.after(dateSelesai)) {
            JOptionPane.showMessageDialog(this, "Tanggal mulai tidak boleh melebihi tanggal selesai!", "Validasi Laporan Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hitung selisih hari
        long diffInMillis = dateSelesai.getTime() - dateMulai.getTime();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        if (diffInDays > 31) {
            JOptionPane.showMessageDialog(this, "Rentang tanggal laporan bulanan tidak boleh melebihi 31 hari!", "Validasi Laporan Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdfFormat.format(dateMulai);
        String end = sdfFormat.format(dateSelesai);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        txtPreview.setText("Mempersiapkan data laporan...");
        btnSaveReport.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                StringBuilder sb = new StringBuilder();
                sb.append("====================================================\n");
                sb.append("             SISTEM INVENTARIS GUDANG               \n");
                sb.append("                 LAPORAN RESMI                      \n");
                sb.append("====================================================\n");
                sb.append("Tanggal Cetak : ").append(timestamp).append("\n");
                sb.append("Dicetak Oleh  : ").append(currentUser.getNamaUser()).append(" (").append(currentUser.getRole()).append(")\n");
                
                if (selectedType == 0) { // Stok Barang
                    sb.append("Tipe Laporan  : Laporan Stok Barang Aktual\n");
                    sb.append("Periode       : ").append(start).append(" s/d ").append(end).append("\n");
                    sb.append("====================================================\n\n");
                    sb.append(String.format("%-10s %-22s %-12s %-6s\n", "KODE", "NAMA BARANG", "KATEGORI", "STOK"));
                    sb.append("----------------------------------------------------\n");
                    
                    List<Barang> items = barangDAO.getAllBarang();
                    double totalNilai = 0;
                    int totalUnit = 0;
                    for (Barang b : items) {
                        String name = b.getNamaBarang();
                        if (name.length() > 20) name = name.substring(0, 18) + "..";
                        sb.append(String.format("%-10s %-22s %-12s %-6d\n", 
                            b.getIdBarang(), name, b.getKategori(), b.getStok()));
                        totalNilai += (b.getHarga() * b.getStok());
                        totalUnit += b.getStok();
                    }
                    sb.append("----------------------------------------------------\n");
                    sb.append("Total Jenis Barang  : ").append(items.size()).append(" jenis\n");
                    sb.append("Total Jumlah Stok   : ").append(totalUnit).append(" unit\n");
                    sb.append("Estimasi Nilai Aset : ").append(String.format("Rp %,.0f", totalNilai)).append("\n");
                    
                } else if (selectedType == 1) { // Barang Masuk
                    sb.append("Tipe Laporan  : Transaksi Barang Masuk (Restock)\n");
                    sb.append("Periode       : ").append(start).append(" s/d ").append(end).append("\n");
                    sb.append("====================================================\n\n");
                    sb.append(String.format("%-8s %-10s %-20s %-5s %-10s\n", "ID", "TANGGAL", "BARANG", "QTY", "SUPPLIER"));
                    sb.append("----------------------------------------------------\n");
                    
                    List<BarangMasuk> items = masukDAO.getAllBarangMasuk();
                    // Filter list by date range period
                    items = items.stream()
                        .filter(bm -> bm.getTanggalMasuk().compareTo(start) >= 0 && bm.getTanggalMasuk().compareTo(end) <= 0)
                        .collect(java.util.stream.Collectors.toList());
                        
                    int totalMasuk = 0;
                    for (BarangMasuk bm : items) {
                        String name = bm.getNamaBarang();
                        if (name.length() > 18) name = name.substring(0, 16) + "..";
                        
                        String supplier = bm.getSupplier();
                        if (supplier.length() > 10) supplier = supplier.substring(0, 8) + "..";

                        sb.append(String.format("%-8s %-10s %-20s %-5d %-10s\n", 
                            bm.getIdBarangMasuk(), bm.getTanggalMasuk(), name, bm.getJumlahMasuk(), supplier));
                        totalMasuk += bm.getJumlahMasuk();
                    }
                    sb.append("----------------------------------------------------\n");
                    sb.append("Total Transaksi Masuk : ").append(items.size()).append(" kali\n");
                    sb.append("Total Unit Masuk      : ").append(totalMasuk).append(" unit\n");
                    
                } else { // Barang Keluar
                    sb.append("Tipe Laporan  : Transaksi Barang Keluar (Distribusi)\n");
                    sb.append("Periode       : ").append(start).append(" s/d ").append(end).append("\n");
                    sb.append("====================================================\n\n");
                    sb.append(String.format("%-8s %-10s %-20s %-5s %-10s\n", "ID", "TANGGAL", "BARANG", "QTY", "PENERIMA"));
                    sb.append("----------------------------------------------------\n");
                    
                    List<BarangKeluar> items = keluarDAO.getAllBarangKeluar();
                    // Filter list by date range period
                    items = items.stream()
                        .filter(bk -> bk.getTanggalKeluar().compareTo(start) >= 0 && bk.getTanggalKeluar().compareTo(end) <= 0)
                        .collect(java.util.stream.Collectors.toList());

                    int totalKeluar = 0;
                    for (BarangKeluar bk : items) {
                        String name = bk.getNamaBarang();
                        if (name.length() > 18) name = name.substring(0, 16) + "..";
                        
                        String penerima = bk.getPenerima();
                        if (penerima.length() > 10) penerima = penerima.substring(0, 8) + "..";

                        sb.append(String.format("%-8s %-10s %-20s %-5d %-10s\n", 
                            bk.getIdBarangKeluar(), bk.getTanggalKeluar(), name, bk.getJumlahKeluar(), penerima));
                        totalKeluar += bk.getJumlahKeluar();
                    }
                    sb.append("----------------------------------------------------\n");
                    sb.append("Total Transaksi Keluar : ").append(items.size()).append(" kali\n");
                    sb.append("Total Unit Keluar      : ").append(totalKeluar).append(" unit\n");
                }
                
                sb.append("====================================================\n");
                sb.append("             * DOKUMEN GENERASI SISTEM *            \n");
                sb.append("====================================================\n");
                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    currentReportContent = get();
                    txtPreview.setText(currentReportContent);
                    btnSaveReport.setEnabled(true);
                    btnExportExcel.setEnabled(true);
                } catch (Exception e) {
                    txtPreview.setText("Gagal membuat laporan: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void saveReportToLog() {
        String jenis = (String) cbJenisLaporan.getSelectedItem();
        String idLog = laporanDAO.generateNewId();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = sdf.format(new Date());

        String keterangan = "";
        if (jenis.contains("Stok")) {
            keterangan = "Audit stok barang. Dihasilkan oleh " + currentUser.getNamaUser();
        } else if (jenis.contains("Masuk")) {
            keterangan = "Audit barang masuk dari supplier. Dihasilkan oleh " + currentUser.getNamaUser();
        } else {
            keterangan = "Audit distribusi barang keluar. Dihasilkan oleh " + currentUser.getNamaUser();
        }

        Laporan l = new Laporan(idLog, tgl, jenis, keterangan);
        
        if (laporanDAO.insertLaporan(l)) {
            JOptionPane.showMessageDialog(this, "Riwayat laporan berhasil disimpan di database audit!");
            btnSaveReport.setEnabled(false);
            loadHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengarsipkan laporan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToExcel() {
        int selectedType = cbJenisLaporan.getSelectedIndex();
        Date dateMulai = dpTanggalMulai.getSelectedDate();
        Date dateSelesai = dpTanggalSelesai.getSelectedDate();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdfDate.format(dateMulai);
        String end = sdfDate.format(dateSelesai);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel (.csv)");
        
        String defaultName = "";
        if (selectedType == 0) {
            defaultName = "Laporan_Stok_Aktual.csv";
        } else if (selectedType == 1) {
            defaultName = "Laporan_Barang_Masuk_" + start + "_to_" + end + ".csv";
        } else {
            defaultName = "Laporan_Barang_Keluar_" + start + "_to_" + end + ".csv";
        }
        fileChooser.setSelectedFile(new java.io.File(defaultName));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
                fileToSave = new java.io.File(filePath);
            }

            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(fileToSave), java.nio.charset.StandardCharsets.UTF_8))) {
                
                // Write UTF-8 BOM for Excel compatibility
                pw.write('\ufeff');

                if (selectedType == 0) {
                    pw.println("KODE BARANG;NAMA BARANG;KATEGORI;STOK;HARGA;NILAI ASET");
                    List<Barang> items = barangDAO.getAllBarang();
                    for (Barang b : items) {
                        pw.println(String.format("%s;%s;%s;%d;%.2f;%.2f",
                            b.getIdBarang(), b.getNamaBarang(), b.getKategori(), b.getStok(), b.getHarga(), (b.getHarga() * b.getStok())));
                    }
                } else if (selectedType == 1) {
                    pw.println("ID MASUK;TANGGAL MASUK;KODE BARANG;NAMA BARANG;JUMLAH MASUK;SUPPLIER");
                    List<BarangMasuk> items = masukDAO.getAllBarangMasuk();
                    items = items.stream()
                        .filter(bm -> bm.getTanggalMasuk().compareTo(start) >= 0 && bm.getTanggalMasuk().compareTo(end) <= 0)
                        .collect(java.util.stream.Collectors.toList());
                    for (BarangMasuk bm : items) {
                        pw.println(String.format("%s;%s;%s;%s;%d;%s",
                            bm.getIdBarangMasuk(), bm.getTanggalMasuk(), bm.getIdBarang(), bm.getNamaBarang(), bm.getJumlahMasuk(), bm.getSupplier()));
                    }
                } else {
                    pw.println("ID KELUAR;TANGGAL KELUAR;KODE BARANG;NAMA BARANG;JUMLAH KELUAR;PENERIMA");
                    List<BarangKeluar> items = keluarDAO.getAllBarangKeluar();
                    items = items.stream()
                        .filter(bk -> bk.getTanggalKeluar().compareTo(start) >= 0 && bk.getTanggalKeluar().compareTo(end) <= 0)
                        .collect(java.util.stream.Collectors.toList());
                    for (BarangKeluar bk : items) {
                        pw.println(String.format("%s;%s;%s;%s;%d;%s",
                            bk.getIdBarangKeluar(), bk.getTanggalKeluar(), bk.getIdBarang(), bk.getNamaBarang(), bk.getJumlahKeluar(), bk.getPenerima()));
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Laporan berhasil diekspor ke Excel (.csv)!", "Sukses Ekspor", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor laporan: " + ex.getMessage(), "Error Ekspor", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
