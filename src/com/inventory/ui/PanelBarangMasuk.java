package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.BarangMasukDAO;
import com.inventory.model.Barang;
import com.inventory.model.BarangMasuk;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelBarangMasuk extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    
    private BarangMasukDAO masukDAO;
    private BarangDAO barangDAO;
    private User currentUser;

    public PanelBarangMasuk(User user) {
        this.currentUser = user;
        this.masukDAO = new BarangMasukDAO();
        this.barangDAO = new BarangDAO();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- 1. HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Transaksi Barang Masuk");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnAdd = new JButton("Input Barang Masuk");
        Theme.styleButton(btnAdd, Theme.SUCCESS, Color.WHITE);
        
        // Only Admin Gudang and Admin Sistem can record incoming goods
        String role = currentUser.getRole();
        if ("Admin Gudang".equalsIgnoreCase(role) || "Admin Sistem".equalsIgnoreCase(role)) {
            headerPanel.add(btnAdd, BorderLayout.EAST);
        } else {
            JLabel lblInfo = new JLabel("Mode: Read-only (Pemilik)");
            lblInfo.setFont(Theme.FONT_SMALL_BOLD);
            lblInfo.setForeground(Theme.FG_MUTED);
            headerPanel.add(lblInfo, BorderLayout.EAST);
        }

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (Table) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"ID Masuk", "Tanggal", "Kode Barang", "Nama Barang", "Jumlah Masuk", "Supplier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        Theme.styleTable(table);
        
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.getViewport().setBackground(Theme.BG_SIDEBAR);
        scrollTable.setBorder(BorderFactory.createLineBorder(Theme.BG_DARK, 1));
        centerPanel.add(scrollTable, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> openInputForm());
    }

    private void loadData() {
        SwingWorker<List<BarangMasuk>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BarangMasuk> doInBackground() {
                return masukDAO.getAllBarangMasuk();
            }

            @Override
            protected void done() {
                try {
                    List<BarangMasuk> list = get();
                    tableModel.setRowCount(0);
                    for (BarangMasuk bm : list) {
                        tableModel.addRow(new Object[]{
                            bm.getIdBarangMasuk(),
                            bm.getTanggalMasuk(),
                            bm.getIdBarang(),
                            bm.getNamaBarang(),
                            bm.getJumlahMasuk(),
                            bm.getSupplier()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openInputForm() {
        // Retrieve products list
        List<Barang> items = barangDAO.getAllBarang();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data barang di sistem! Tambah barang di Master Barang terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Input Barang Masuk Baru", true);
        dialog.setSize(400, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Theme.BG_SIDEBAR);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Fields
        JLabel lblId = new JLabel("ID Barang Masuk");
        lblId.setForeground(Theme.FG_MUTED);
        JTextField txtId = new JTextField(masukDAO.generateNewId());
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setEnabled(false);

        JLabel lblTanggal = new JLabel("Tanggal Masuk (YYYY-MM-DD)");
        lblTanggal.setForeground(Theme.FG_LIGHT);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField txtTanggal = new JTextField(sdf.format(new Date()));
        txtTanggal.setBackground(Theme.BG_DARK);
        txtTanggal.setForeground(Theme.FG_LIGHT);
        txtTanggal.setCaretColor(Theme.FG_LIGHT);

        JLabel lblBarang = new JLabel("Pilih Barang");
        lblBarang.setForeground(Theme.FG_LIGHT);
        
        // Create custom list representations for ComboBox
        JComboBox<String> cbBarang = new JComboBox<>();
        cbBarang.setBackground(Theme.BG_DARK);
        cbBarang.setForeground(Theme.FG_LIGHT);
        for (Barang b : items) {
            cbBarang.addItem(b.getIdBarang() + " - " + b.getNamaBarang() + " (Stok: " + b.getStok() + ")");
        }

        JLabel lblJumlah = new JLabel("Jumlah Masuk");
        lblJumlah.setForeground(Theme.FG_LIGHT);
        JTextField txtJumlah = new JTextField();
        txtJumlah.setBackground(Theme.BG_DARK);
        txtJumlah.setForeground(Theme.FG_LIGHT);
        txtJumlah.setCaretColor(Theme.FG_LIGHT);

        JLabel lblSupplier = new JLabel("Supplier");
        lblSupplier.setForeground(Theme.FG_LIGHT);
        JTextField txtSupplier = new JTextField();
        txtSupplier.setBackground(Theme.BG_DARK);
        txtSupplier.setForeground(Theme.FG_LIGHT);
        txtSupplier.setCaretColor(Theme.FG_LIGHT);

        // Layout adding
        gbc.gridx = 0; gbc.gridy = 0; content.add(lblId, gbc);
        gbc.gridx = 1; content.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; content.add(lblTanggal, gbc);
        gbc.gridx = 1; content.add(txtTanggal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; content.add(lblBarang, gbc);
        gbc.gridx = 1; content.add(cbBarang, gbc);

        gbc.gridx = 0; gbc.gridy = 3; content.add(lblJumlah, gbc);
        gbc.gridx = 1; content.add(txtJumlah, gbc);

        gbc.gridx = 0; gbc.gridy = 4; content.add(lblSupplier, gbc);
        gbc.gridx = 1; content.add(txtSupplier, gbc);

        // Buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.SUCCESS, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String idMasuk = txtId.getText();
            String tanggal = txtTanggal.getText().trim();
            int selectedIdx = cbBarang.getSelectedIndex();
            String jumlahStr = txtJumlah.getText().trim();
            String supplier = txtSupplier.getText().trim();

            if (tanggal.isEmpty() || jumlahStr.isEmpty() || supplier.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlah;
            try {
                jumlah = Integer.parseInt(jumlahStr);
                if (jumlah <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah masuk harus berupa angka positif!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract item ID
            Barang selectedBarang = items.get(selectedIdx);
            
            BarangMasuk bm = new BarangMasuk(idMasuk, tanggal, selectedBarang.getIdBarang(), selectedBarang.getNamaBarang(), jumlah, supplier);
            
            if (masukDAO.insertBarangMasuk(bm)) {
                JOptionPane.showMessageDialog(dialog, "Data barang masuk berhasil dicatat, stok otomatis bertambah!");
                dialog.dispose();
                loadData();
                
                // If the dashboard panel is open, this might need refresh, MainFrame handles panel refreshes
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof MainFrame) {
                    ((MainFrame) parentWindow).refreshDashboardData();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal mencatat data barang masuk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
