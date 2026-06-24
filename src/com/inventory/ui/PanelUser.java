package com.inventory.ui;

import com.inventory.dao.UserDAO;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelUser extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete;

    private UserDAO userDAO;
    private User currentUser;

    public PanelUser(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();

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
        
        JLabel lblTitle = new JLabel("Kelola Akun Pengguna");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnAdd = new JButton("Tambah Pengguna");
        btnEdit = new JButton("Ubah Akun");
        btnDelete = new JButton("Hapus");

        Theme.styleButton(btnAdd, Theme.SUCCESS, Color.WHITE);
        Theme.styleButton(btnEdit, Theme.PRIMARY, Color.WHITE);
        Theme.styleButton(btnDelete, Theme.DANGER, Color.WHITE);

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (Table) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"ID User", "Username", "Role", "Nama Lengkap"};
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
        btnAdd.addActionListener(e -> openUserForm(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                User userToEdit = userDAO.getAllUsers().stream()
                        .filter(u -> u.getIdUser() == id)
                        .findFirst()
                        .orElse(null);
                
                if (userToEdit != null) {
                    openUserForm(userToEdit);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris pengguna yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String username = (String) tableModel.getValueAt(selectedRow, 1);
                
                // Self deletion protection
                if (currentUser.getIdUser() == id) {
                    JOptionPane.showMessageDialog(this, "Anda tidak dapat menghapus akun Anda sendiri yang sedang aktif!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda yakin ingin menghapus akun pengguna '" + username + "'?", 
                    "Konfirmasi Hapus Pengguna", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (userDAO.deleteUser(id)) {
                        JOptionPane.showMessageDialog(this, "Akun berhasil dihapus!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus akun!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris pengguna yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void loadData() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return userDAO.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> list = get();
                    tableModel.setRowCount(0);
                    for (User u : list) {
                        tableModel.addRow(new Object[]{
                            u.getIdUser(),
                            u.getUsername(),
                            u.getRole(),
                            u.getNamaUser()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openUserForm(User user) {
        boolean isEdit = (user != null);
        String title = isEdit ? "Ubah Akun Pengguna" : "Tambah Pengguna Baru";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(400, 320);
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
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(Theme.FG_LIGHT);
        JTextField txtUser = new JTextField();
        txtUser.setBackground(Theme.BG_DARK);
        txtUser.setForeground(Theme.FG_LIGHT);
        txtUser.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) txtUser.setText(user.getUsername());

        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(Theme.FG_LIGHT);
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBackground(Theme.BG_DARK);
        txtPass.setForeground(Theme.FG_LIGHT);
        txtPass.setCaretColor(Theme.FG_LIGHT);
        // Leave password field empty (blank) on edit

        JLabel lblRole = new JLabel("Role / Otoritas");
        lblRole.setForeground(Theme.FG_LIGHT);
        String[] roleOptions = {"Admin Sistem", "Admin Gudang", "Pemilik"};
        JComboBox<String> cbRole = new JComboBox<>(roleOptions);
        cbRole.setBackground(Theme.BG_DARK);
        cbRole.setForeground(Theme.FG_LIGHT);
        if (isEdit) cbRole.setSelectedItem(user.getRole());

        JLabel lblNama = new JLabel("Nama Lengkap");
        lblNama.setForeground(Theme.FG_LIGHT);
        JTextField txtNama = new JTextField();
        txtNama.setBackground(Theme.BG_DARK);
        txtNama.setForeground(Theme.FG_LIGHT);
        txtNama.setCaretColor(Theme.FG_LIGHT);
        if (isEdit) txtNama.setText(user.getNamaUser());

        // Layout adding
        gbc.gridx = 0; gbc.gridy = 0; content.add(lblUser, gbc);
        gbc.gridx = 1; content.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; content.add(lblPass, gbc);
        gbc.gridx = 1; content.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; content.add(lblRole, gbc);
        gbc.gridx = 1; content.add(cbRole, gbc);

        gbc.gridx = 0; gbc.gridy = 3; content.add(lblNama, gbc);
        gbc.gridx = 1; content.add(txtNama, gbc);

        // Buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        JButton btnCancel = new JButton("Batal");
        JButton btnSave = new JButton("Simpan");
        Theme.styleButton(btnCancel, Theme.BG_CARD_ALT, Theme.FG_LIGHT);
        Theme.styleButton(btnSave, Theme.SUCCESS, Color.WHITE);
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        content.add(buttonRow, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            String role = (String) cbRole.getSelectedItem();
            String nama = txtNama.getText().trim();

            if (username.isEmpty() || nama.isEmpty() || (!isEdit && password.isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Username, Nama Lengkap" + (!isEdit ? ", dan Password" : "") + " wajib diisi!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User u = new User();
            u.setUsername(username);
            u.setPassword(password);
            u.setRole(role);
            u.setNamaUser(nama);

            boolean success;
            if (isEdit) {
                u.setIdUser(user.getIdUser());
                success = userDAO.updateUser(u);
            } else {
                success = userDAO.insertUser(u);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Akun pengguna berhasil disimpan!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan akun! Username mungkin sudah digunakan.", "Error", JOptionPane.ERROR_MESSAGE);
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
