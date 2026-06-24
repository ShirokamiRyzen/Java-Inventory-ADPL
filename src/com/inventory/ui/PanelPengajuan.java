package com.inventory.ui;

import com.inventory.dao.BarangDAO;
import com.inventory.dao.PengajuanPembelianDAO;
import com.inventory.model.Barang;
import com.inventory.model.PengajuanPembelian;
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

public class PanelPengajuan extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnApprove, btnReject;

    private PengajuanPembelianDAO pengajuanDAO;
    private BarangDAO barangDAO;
    private User currentUser;

    public PanelPengajuan(User user) {
        this.currentUser = user;
        this.pengajuanDAO = new PengajuanPembelianDAO();
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
        
        JLabel lblTitle = new JLabel("Pengajuan Pembelian Barang");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnAdd = new JButton("Buat Pengajuan Baru");
        btnApprove = new JButton("Setujui");
        btnReject = new JButton("Tolak");

        Theme.styleButton(btnAdd, Theme.PRIMARY, Color.WHITE);
        Theme.styleButton(btnApprove, Theme.SUCCESS, Color.WHITE);
        Theme.styleButton(btnReject, Theme.DANGER, Color.WHITE);

        String role = currentUser.getRole();
        if ("Pemilik".equalsIgnoreCase(role)) {
            // Owner can approve / reject requests
            actionPanel.add(btnApprove);
            actionPanel.add(btnReject);
        } else {
            // Admins can submit new requests
            actionPanel.add(btnAdd);
        }

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (Table) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"ID Pengajuan", "Kode Barang", "Nama Barang", "Tanggal Pengajuan", "Jumlah", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        Theme.styleTable(table);
        
        // Custom cell renderer to color status column (Pending=Amber, Disetujui=Emerald, Ditolak=Rose)
        table.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                
                String status = (String) value;
                if (!isSelected) {
                    if ("Disetujui".equalsIgnoreCase(status)) {
                        c.setForeground(Theme.SUCCESS);
                    } else if ("Ditolak".equalsIgnoreCase(status)) {
                        c.setForeground(Theme.DANGER);
                    } else {
                        c.setForeground(Theme.WARNING); // Pending
                    }
                    c.setFont(Theme.FONT_SMALL_BOLD);
                }
                return c;
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.getViewport().setBackground(Theme.BG_SIDEBAR);
        scrollTable.setBorder(BorderFactory.createLineBorder(Theme.BG_DARK, 1));
        centerPanel.add(scrollTable, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        btnAdd.addActionListener(e -> openRequestForm());
        btnApprove.addActionListener(e -> handleStatusChange("Disetujui"));
        btnReject.addActionListener(e -> handleStatusChange("Ditolak"));
    }

    private void loadData() {
        SwingWorker<List<PengajuanPembelian>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PengajuanPembelian> doInBackground() {
                return pengajuanDAO.getAllPengajuan();
            }

            @Override
            protected void done() {
                try {
                    List<PengajuanPembelian> list = get();
                    tableModel.setRowCount(0);
                    for (PengajuanPembelian pp : list) {
                        tableModel.addRow(new Object[]{
                            pp.getIdPengajuan(),
                            pp.getIdBarang(),
                            pp.getNamaBarang(),
                            pp.getTanggalPengajuan(),
                            pp.getJumlahPengajuan(),
                            pp.getStatusPengajuan()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openRequestForm() {
        List<Barang> items = barangDAO.getAllBarang();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data barang di sistem!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Buat Pengajuan Pembelian", true);
        dialog.setSize(450, 320); // slightly wider to avoid any label cutoffs
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Theme.BG_SIDEBAR);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Fields
        JLabel lblId = new JLabel("ID Pengajuan");
        lblId.setForeground(Theme.FG_MUTED);
        JTextField txtId = new JTextField(pengajuanDAO.generateNewId());
        txtId.setBackground(Theme.BG_DARK);
        txtId.setForeground(Theme.FG_LIGHT);
        txtId.setEnabled(false);

        JLabel lblTanggal = new JLabel("Tanggal Pengajuan");
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

        JLabel lblJumlah = new JLabel("Jumlah Pengajuan");
        lblJumlah.setForeground(Theme.FG_LIGHT);
        JTextField txtJumlah = new JTextField();
        txtJumlah.setBackground(Theme.BG_DARK);
        txtJumlah.setForeground(Theme.FG_LIGHT);
        txtJumlah.setCaretColor(Theme.FG_LIGHT);

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

        // Buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Ajukan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.PRIMARY, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String idPengajuan = txtId.getText();
            SimpleDateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = sdfFormat.format(dpTanggal.getSelectedDate());
            int selectedIdx = cbBarang.getSelectedIndex();
            String jumlahStr = txtJumlah.getText().trim();

            if (tanggal.isEmpty() || jumlahStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int jumlah;
            try {
                jumlah = Integer.parseInt(jumlahStr);
                if (jumlah <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah harus berupa angka positif!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Barang selectedBarang = items.get(selectedIdx);
            
            PengajuanPembelian pp = new PengajuanPembelian(idPengajuan, selectedBarang.getIdBarang(), selectedBarang.getNamaBarang(), tanggal, jumlah, "Pending");
            
            if (pengajuanDAO.insertPengajuan(pp)) {
                JOptionPane.showMessageDialog(dialog, "Pengajuan pembelian berhasil dikirim! Menunggu persetujuan pemilik.");
                dialog.dispose();
                loadData();
                
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof MainFrame) {
                    ((MainFrame) parentWindow).refreshDashboardData();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal membuat pengajuan pembelian!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void handleStatusChange(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

            if (!"Pending".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Pengajuan ini sudah berstatus '" + currentStatus + "' dan tidak dapat diubah lagi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Apakah Anda yakin ingin memproses pengajuan '" + id + "' dengan status: " + newStatus + "?", 
                "Konfirmasi Verifikasi", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (pengajuanDAO.updateStatus(id, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Pengajuan berhasil diperbarui ke status: " + newStatus);
                    loadData();
                    
                    Window parentWindow = SwingUtilities.getWindowAncestor(this);
                    if (parentWindow instanceof MainFrame) {
                        ((MainFrame) parentWindow).refreshDashboardData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui status pengajuan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris pengajuan yang ingin diproses!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void clearTableSelection() {
        if (table != null) {
            table.clearSelection();
        }
    }
}
