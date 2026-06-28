package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.BarangMasukDAO;
import com.inventory.dao.SupplierDAO;
import com.inventory.model.Barang;
import com.inventory.model.BarangMasuk;
import com.inventory.model.Supplier;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;
import com.inventory.ui.components.DatePicker;

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
    private SupplierDAO supplierDAO;
    private User currentUser;

    public PanelBarangMasuk(User user) {
        this.currentUser = user;
        this.masukDAO = new BarangMasukDAO();
        this.barangDAO = new BarangDAO();
        this.supplierDAO = new SupplierDAO();

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

        String[] columns = {"ID Masuk", "Tanggal", "Kode Barang", "Nama Barang", "Jumlah Masuk", "Supplier", "Harga Beli"};
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
                            bm.getIdSupplier(),
                            String.format("Rp %,.0f", bm.getHargaBeli())
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

        // Retrieve suppliers list
        List<Supplier> suppliers = supplierDAO.getAllSupplier();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Input Barang Masuk Baru", true);
        dialog.setSize(450, 420); // taller to fit all elements comfortably
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Theme.BG_SIDEBAR);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Fields
        JLabel lblId = new JLabel("ID Barang Masuk");
        lblId.setForeground(Theme.FG_MUTED);
        JTextField txtId = new JTextField(masukDAO.generateNewId());
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setEnabled(false);

        JLabel lblTanggal = new JLabel("Tanggal Masuk");
        lblTanggal.setForeground(Theme.FG_LIGHT);
        DatePicker dpTanggal = new DatePicker(new Date());

        JLabel lblBarang = new JLabel("Pilih Barang");
        lblBarang.setForeground(Theme.FG_LIGHT);
        
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

        JLabel lblHargaBeli = new JLabel("Harga Beli (Rp)");
        lblHargaBeli.setForeground(Theme.FG_LIGHT);
        JTextField txtHargaBeli = new JTextField();
        txtHargaBeli.setBackground(Theme.BG_DARK);
        txtHargaBeli.setForeground(Theme.FG_LIGHT);
        txtHargaBeli.setCaretColor(Theme.FG_LIGHT);

        JLabel lblSupplier = new JLabel("Supplier");
        lblSupplier.setForeground(Theme.FG_LIGHT);
        
        // Editable combo box for supplier selection/creation
        JComboBox<String> cbSupplier = new JComboBox<>();
        cbSupplier.setBackground(Theme.BG_DARK);
        cbSupplier.setForeground(Theme.FG_LIGHT);
        cbSupplier.setEditable(true);
        for (Supplier s : suppliers) {
            cbSupplier.addItem(s.getIdSupplier());
        }

        // Auto-fill price based on chosen product
        cbBarang.addActionListener(e -> {
            int idx = cbBarang.getSelectedIndex();
            if (idx >= 0) {
                Barang selected = items.get(idx);
                txtHargaBeli.setText(String.format("%.0f", selected.getHargaBeli()));
            }
        });

        // Trigger action once to initialize first item price
        if (cbBarang.getSelectedIndex() >= 0) {
            Barang selected = items.get(cbBarang.getSelectedIndex());
            txtHargaBeli.setText(String.format("%.0f", selected.getHargaBeli()));
        }

        // Layout adding (explicit column weights so labels don't get truncated)
        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 0; content.add(lblId, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(txtId, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 1; content.add(lblTanggal, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(dpTanggal, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 2; content.add(lblBarang, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(cbBarang, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 3; content.add(lblJumlah, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(txtJumlah, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 4; content.add(lblHargaBeli, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(txtHargaBeli, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = 5; content.add(lblSupplier, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(cbSupplier, gbc);

        // Buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.SUCCESS, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String idMasuk = txtId.getText();
            SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = sdfFormat.format(dpTanggal.getSelectedDate());
            int selectedIdx = cbBarang.getSelectedIndex();
            String jumlahStr = txtJumlah.getText().trim();
            String hargaBeliStr = txtHargaBeli.getText().trim();
            
            Object selectedSuppObj = cbSupplier.getSelectedItem();
            String supplierStr = selectedSuppObj != null ? selectedSuppObj.toString().trim() : "";

            if (tanggal.isEmpty() || jumlahStr.isEmpty() || hargaBeliStr.isEmpty() || supplierStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlah;
            double hargaBeli;
            try {
                jumlah = Integer.parseInt(jumlahStr);
                hargaBeli = Double.parseDouble(hargaBeliStr);
                if (jumlah <= 0 || hargaBeli < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah masuk dan Harga beli harus berupa angka positif!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract item ID
            Barang selectedBarang = items.get(selectedIdx);

            // Handle Supplier DB registration if it's a new supplier
            Supplier existingSupp = supplierDAO.getSupplierById(supplierStr);
            if (existingSupp == null) {
                String telp = JOptionPane.showInputDialog(dialog, 
                    "Supplier baru '" + supplierStr + "' terdeteksi.\nMasukkan nomor telepon supplier:", 
                    "Supplier Baru", 
                    JOptionPane.QUESTION_MESSAGE
                );
                if (telp == null) {
                    return; // user cancelled
                }
                Supplier s = new Supplier(supplierStr, telp.trim().isEmpty() ? "-" : telp.trim());
                if (!supplierDAO.insertSupplier(s)) {
                    JOptionPane.showMessageDialog(dialog, "Gagal membuat supplier baru di database!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            BarangMasuk bm = new BarangMasuk(idMasuk, tanggal, selectedBarang.getIdBarang(), selectedBarang.getNamaBarang(), jumlah, supplierStr, hargaBeli);
            
            if (masukDAO.insertBarangMasuk(bm)) {
                JOptionPane.showMessageDialog(dialog, "Data barang masuk berhasil dicatat, stok otomatis bertambah!");
                dialog.dispose();
                loadData();
                
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

    public void clearTableSelection() {
        if (table != null) {
            table.clearSelection();
        }
    }
}
