package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.BarangKeluarDAO;
import com.inventory.model.Barang;
import com.inventory.model.BarangKeluar;
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

public class PanelBarangKeluar extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    
    private BarangKeluarDAO keluarDAO;
    private BarangDAO barangDAO;
    private User currentUser;

    public PanelBarangKeluar(User user) {
        this.currentUser = user;
        this.keluarDAO = new BarangKeluarDAO();
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
        
        JLabel lblTitle = new JLabel("Transaksi Barang Keluar");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnAdd = new JButton("Input Barang Keluar");
        Theme.styleButton(btnAdd, Theme.DANGER, Color.WHITE);

        // Only Admin Gudang and Admin Sistem can record outgoing goods
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

        String[] columns = {"ID Keluar", "Tanggal", "Kode Barang", "Nama Barang", "Jumlah Keluar", "Penerima"};
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
        SwingWorker<List<BarangKeluar>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BarangKeluar> doInBackground() {
                return keluarDAO.getAllBarangKeluar();
            }

            @Override
            protected void done() {
                try {
                    List<BarangKeluar> list = get();
                    tableModel.setRowCount(0);
                    for (BarangKeluar bk : list) {
                        tableModel.addRow(new Object[]{
                            bk.getIdBarangKeluar(),
                            bk.getTanggalKeluar(),
                            bk.getIdBarang(),
                            bk.getNamaBarang(),
                            bk.getJumlahKeluar(),
                            bk.getPenerima()
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
        List<Barang> items = barangDAO.getAllBarang();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data barang di sistem!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Input Barang Keluar Baru", true);
        dialog.setSize(450, 360); // slightly wider to avoid any label cutoffs
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Theme.BG_SIDEBAR);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Fields
        JLabel lblId = new JLabel("ID Barang Keluar");
        lblId.setForeground(Theme.FG_MUTED);
        JTextField txtId = new JTextField(keluarDAO.generateNewId());
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setEnabled(false);

        JLabel lblTanggal = new JLabel("Tanggal Keluar");
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

        JLabel lblJumlah = new JLabel("Jumlah Keluar");
        lblJumlah.setForeground(Theme.FG_LIGHT);
        JTextField txtJumlah = new JTextField();
        txtJumlah.setBackground(Theme.BG_DARK);
        txtJumlah.setForeground(Theme.FG_LIGHT);
        txtJumlah.setCaretColor(Theme.FG_LIGHT);

        JLabel lblPenerima = new JLabel("Penerima / Tujuan");
        lblPenerima.setForeground(Theme.FG_LIGHT);
        JTextField txtPenerima = new JTextField();
        txtPenerima.setBackground(Theme.BG_DARK);
        txtPenerima.setForeground(Theme.FG_LIGHT);
        txtPenerima.setCaretColor(Theme.FG_LIGHT);

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
        gbc.gridx = 0; gbc.gridy = 4; content.add(lblPenerima, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1; content.add(txtPenerima, gbc);

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.DANGER, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String idKeluar = txtId.getText();
            SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = sdfFormat.format(dpTanggal.getSelectedDate());
            int selectedIdx = cbBarang.getSelectedIndex();
            String jumlahStr = txtJumlah.getText().trim();
            String penerima = txtPenerima.getText().trim();

            if (tanggal.isEmpty() || jumlahStr.isEmpty() || penerima.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlah;
            try {
                jumlah = Integer.parseInt(jumlahStr);
                if (jumlah <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah keluar harus berupa angka positif!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Barang selectedBarang = items.get(selectedIdx);
            
            // Check stock before submitting database call (optional safety layer, DAO will do it transactionally too)
            if (selectedBarang.getStok() < jumlah) {
                JOptionPane.showMessageDialog(dialog, "Stok tidak mencukupi!\nStok saat ini: " + selectedBarang.getStok() + "\nJumlah diminta: " + jumlah, "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BarangKeluar bk = new BarangKeluar(idKeluar, tanggal, selectedBarang.getIdBarang(), selectedBarang.getNamaBarang(), jumlah, penerima);
            
            if (keluarDAO.insertBarangKeluar(bk)) {
                JOptionPane.showMessageDialog(dialog, "Data barang keluar berhasil dicatat, stok berkurang!");
                dialog.dispose();
                loadData();
                
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof MainFrame) {
                    ((MainFrame) parentWindow).refreshDashboardData();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal mencatat data barang keluar! Periksa stok barang kembali.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
