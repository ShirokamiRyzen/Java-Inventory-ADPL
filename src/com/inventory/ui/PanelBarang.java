package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.model.Barang;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelBarang extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnAdd, btnEdit, btnDelete;
    
    private BarangDAO barangDAO;
    private User currentUser;
    private List<Barang> allItems;

    public PanelBarang(User user) {
        this.currentUser = user;
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
        
        JLabel lblTitle = new JLabel("Data Master Barang");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Action Buttons Panel (Add, Edit, Delete)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        btnAdd = new JButton("Tambah Barang");
        btnEdit = new JButton("Ubah Barang");
        btnDelete = new JButton("Hapus");

        Theme.styleButton(btnAdd, Theme.SUCCESS, Color.WHITE);
        Theme.styleButton(btnEdit, Theme.PRIMARY, Color.WHITE);
        Theme.styleButton(btnDelete, Theme.DANGER, Color.WHITE);

        // Apply Role-based access control
        String role = currentUser.getRole();
        if ("Admin Sistem".equalsIgnoreCase(role)) {
            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
            actionPanel.add(btnDelete);
        } else if ("Pemilik".equalsIgnoreCase(role)) {
            // Pemilik can add new goods and adjust prices
            btnEdit.setText("Sesuaikan Harga");
            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
        } else {
            // Admin Gudang can only view/monitor
            JLabel lblInfo = new JLabel("Mode: Read-only (Pemantauan)");
            lblInfo.setFont(Theme.FONT_SMALL_BOLD);
            lblInfo.setForeground(Theme.FG_MUTED);
            actionPanel.add(lblInfo);
        }

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (Table and Search) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Search Bar panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        searchBarPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Cari Barang:   ");
        lblSearch.setFont(Theme.FONT_HEADER);
        lblSearch.setForeground(Theme.FG_LIGHT);
        searchBarPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 35));
        txtSearch.setFont(Theme.FONT_BODY);
        txtSearch.setBackground(Theme.BG_SIDEBAR);
        txtSearch.setForeground(Theme.FG_LIGHT);
        txtSearch.setCaretColor(Theme.FG_LIGHT);
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik nama barang atau kategori...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterData(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
        searchBarPanel.add(txtSearch);
        centerPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Kode Barang", "Nama Barang", "Kategori", "Harga", "Stok"};
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

        // Action Listeners
        btnAdd.addActionListener(e -> openFormDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                Barang barang = barangDAO.getBarangById(id);
                if (barang != null) {
                    openFormDialog(barang);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris barang yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda yakin ingin menghapus barang '" + name + "'?\nIni juga akan menghapus riwayat masuk/keluar barang tersebut.", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (barangDAO.deleteBarang(id)) {
                        JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus barang!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris barang yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void loadData() {
        SwingWorker<List<Barang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Barang> doInBackground() {
                return barangDAO.getAllBarang();
            }

            @Override
            protected void done() {
                try {
                    allItems = get();
                    filterData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void filterData() {
        if (allItems == null) return;
        String query = txtSearch.getText().toLowerCase().trim();
        tableModel.setRowCount(0);

        List<Barang> filtered = allItems.stream()
                .filter(b -> b.getNamaBarang().toLowerCase().contains(query) ||
                             b.getKategori().toLowerCase().contains(query) ||
                             b.getIdBarang().toLowerCase().contains(query))
                .collect(Collectors.toList());

        for (Barang b : filtered) {
            tableModel.addRow(new Object[]{
                b.getIdBarang(),
                b.getNamaBarang(),
                b.getKategori(),
                String.format("Rp %,.0f", b.getHarga()),
                b.getStok()
            });
        }
    }

    private void openFormDialog(Barang barang) {
        boolean isEdit = (barang != null);
        String title = isEdit ? (currentUser.getRole().equals("Pemilik") ? "Sesuaikan Harga Barang" : "Ubah Data Barang") : "Tambah Barang Baru";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(400, 380);
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
        JLabel lblId = new JLabel("Kode Barang");
        lblId.setForeground(Theme.FG_MUTED);
        JTextField txtId = new JTextField();
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setEnabled(false); // ID is read-only
        if (isEdit) {
            txtId.setText(barang.getIdBarang());
        } else {
            txtId.setText(barangDAO.generateNewId());
        }

        JLabel lblNama = new JLabel("Nama Barang");
        lblNama.setForeground(Theme.FG_LIGHT);
        JTextField txtNama = new JTextField();
        txtNama.setBackground(Theme.BG_DARK);
        txtNama.setForeground(Theme.FG_LIGHT);
        txtNama.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) {
            txtNama.setText(barang.getNamaBarang());
            if (currentUser.getRole().equals("Pemilik")) {
                txtNama.setEnabled(false); // Owner can only change price
            }
        }

        JLabel lblKategori = new JLabel("Kategori");
        lblKategori.setForeground(Theme.FG_LIGHT);
        String[] kategoriOptions = {"ATK", "Elektronik", "Bahan Pokok", "Peralatan", "Lain-lain"};
        JComboBox<String> cbKategori = new JComboBox<>(kategoriOptions);
        cbKategori.setBackground(Theme.BG_DARK);
        cbKategori.setForeground(Theme.FG_LIGHT);
        if (isEdit) {
            cbKategori.setSelectedItem(barang.getKategori());
            if (currentUser.getRole().equals("Pemilik")) {
                cbKategori.setEnabled(false); // Owner can only change price
            }
        }

        JLabel lblHarga = new JLabel("Harga (Rp)");
        lblHarga.setForeground(Theme.FG_LIGHT);
        JTextField txtHarga = new JTextField();
        txtHarga.setBackground(Theme.BG_DARK);
        txtHarga.setForeground(Theme.FG_LIGHT);
        txtHarga.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) {
            txtHarga.setText(String.format("%.0f", barang.getHarga()));
        }

        JLabel lblStok = new JLabel("Stok Awal");
        lblStok.setForeground(Theme.FG_LIGHT);
        JTextField txtStok = new JTextField("0");
        txtStok.setBackground(Theme.BG_DARK);
        txtStok.setForeground(Theme.FG_LIGHT);
        txtStok.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) {
            txtStok.setText(String.valueOf(barang.getStok()));
            txtStok.setEnabled(false); // Stok is edited through Goods In/Out, never manually override!
        }

        // Layout adding
        gbc.gridx = 0; gbc.gridy = 0; content.add(lblId, gbc);
        gbc.gridx = 1; content.add(txtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; content.add(lblNama, gbc);
        gbc.gridx = 1; content.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy = 2; content.add(lblKategori, gbc);
        gbc.gridx = 1; content.add(cbKategori, gbc);

        gbc.gridx = 0; gbc.gridy = 3; content.add(lblHarga, gbc);
        gbc.gridx = 1; content.add(txtHarga, gbc);

        gbc.gridx = 0; gbc.gridy = 4; content.add(lblStok, gbc);
        gbc.gridx = 1; content.add(txtStok, gbc);

        // Buttons
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
            // Validation
            String nama = txtNama.getText().trim();
            String kategori = (String) cbKategori.getSelectedItem();
            String hargaStr = txtHarga.getText().trim();
            String stokStr = txtStok.getText().trim();

            if (nama.isEmpty() || hargaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field harus diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double harga;
            int stok;
            try {
                harga = Double.parseDouble(hargaStr);
                stok = Integer.parseInt(stokStr);
                if (harga < 0 || stok < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Harga dan Stok harus berupa angka positif!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Barang b = new Barang(txtId.getText(), nama, harga, kategori, stok);
            boolean success;
            if (isEdit) {
                success = barangDAO.updateBarang(b);
            } else {
                success = barangDAO.insertBarang(b);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Data barang berhasil disimpan!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data barang!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(content);
        dialog.setVisible(true);
     }

    public void clearTableSelection() {
        if (table != null) {
            table.clearSelection();
        }
        if (txtSearch != null) {
            txtSearch.setText("");
        }
    }
}
