package com.inventory.ui;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelSupplier extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;

    private SupplierDAO supplierDAO;
    private User currentUser;

    public PanelSupplier(User user) {
        this.currentUser = user;
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
        
        JLabel lblTitle = new JLabel("Kelola Master Supplier");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnAdd = new JButton("Tambah Supplier");
        btnEdit = new JButton("Ubah Data");
        btnDelete = new JButton("Hapus");

        Theme.styleButton(btnAdd, Theme.SUCCESS, Color.WHITE);
        Theme.styleButton(btnEdit, Theme.PRIMARY, Color.WHITE);
        Theme.styleButton(btnDelete, Theme.DANGER, Color.WHITE);

        String role = currentUser.getRole();
        if ("Admin Sistem".equalsIgnoreCase(role) || "Admin Gudang".equalsIgnoreCase(role)) {
            actionPanel.add(btnAdd);
            actionPanel.add(btnEdit);
            actionPanel.add(btnDelete);
        } else {
            JLabel lblInfo = new JLabel("Mode: Read-only (Pemilik)");
            lblInfo.setFont(Theme.FONT_SMALL_BOLD);
            lblInfo.setForeground(Theme.FG_MUTED);
            actionPanel.add(lblInfo);
        }

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (Table) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"ID Supplier / Nama", "Nomor Telepon"};
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
        btnAdd.addActionListener(e -> openSupplierForm(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                Supplier supplier = supplierDAO.getSupplierById(id);
                if (supplier != null) {
                    openSupplierForm(supplier);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris supplier yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda yakin ingin menghapus supplier '" + id + "'?\nIni juga akan menghapus riwayat transaksi dari supplier tersebut.", 
                    "Konfirmasi Hapus Supplier", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (supplierDAO.deleteSupplier(id)) {
                        JOptionPane.showMessageDialog(this, "Supplier berhasil dihapus!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus supplier! Supplier mungkin sedang direferensikan oleh data barang masuk.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris supplier yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void loadData() {
        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Supplier> doInBackground() {
                return supplierDAO.getAllSupplier();
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> list = get();
                    tableModel.setRowCount(0);
                    for (Supplier s : list) {
                        tableModel.addRow(new Object[]{
                            s.getIdSupplier(),
                            s.getNomorTelepon()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openSupplierForm(Supplier s) {
        boolean isEdit = (s != null);
        String title = isEdit ? "Ubah Data Supplier" : "Tambah Supplier Baru";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(400, 240);
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
        JLabel lblId = new JLabel("Nama Supplier");
        lblId.setForeground(Theme.FG_LIGHT);
        JTextField txtId = new JTextField();
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) {
            txtId.setText(s.getIdSupplier());
            txtId.setEnabled(false); // ID is PK, cannot be modified
        }

        JLabel lblNoTelp = new JLabel("Nomor Telepon");
        lblNoTelp.setForeground(Theme.FG_LIGHT);
        JTextField txtNoTelp = new JTextField();
        txtNoTelp.setBackground(Theme.BG_DARK);
        txtNoTelp.setForeground(Theme.FG_LIGHT);
        txtNoTelp.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) {
            txtNoTelp.setText(s.getNomorTelepon());
        }

        gbc.gridx = 0; gbc.gridy = 0; content.add(lblId, gbc);
        gbc.gridx = 1; content.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; content.add(lblNoTelp, gbc);
        gbc.gridx = 1; content.add(txtNoTelp, gbc);

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.SUCCESS, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e2 -> dialog.dispose());
        btnSave.addActionListener(e2 -> {
            String name = txtId.getText().trim();
            String noTelp = txtNoTelp.getText().trim();

            if (name.isEmpty() || noTelp.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field harus diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Supplier supplier = new Supplier(name, noTelp);
            boolean success;
            if (isEdit) {
                success = supplierDAO.updateSupplier(supplier);
            } else {
                success = supplierDAO.insertSupplier(supplier);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Data supplier berhasil disimpan!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data supplier! Supplier mungkin sudah terdaftar.", "Error", JOptionPane.ERROR_MESSAGE);
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
