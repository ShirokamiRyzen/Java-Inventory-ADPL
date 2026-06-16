package com.inventory.ui;

import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private Map<String, JPanel> viewPanels = new HashMap<>();
    private JPanel activeMenuButton = null;

    public MainFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Sistem Manajemen Inventaris Gudang - " + currentUser.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        com.inventory.ui.theme.WindowState.loadState(this, 1200, 750);

        // Save window bounds on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                com.inventory.ui.theme.WindowState.saveState(MainFrame.this);
            }
        });

        // Main Container
        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.setBackground(Theme.BG_DARK);
        setContentPane(mainPane);

        // --- 1. SIDEBAR (Left) ---
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Theme.BG_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BG_DARK));

        // Sidebar Header
        JPanel sbHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        sbHeader.setOpaque(false);
        
        JLabel lblLogo = new JLabel("⚡"); // Decorative modern icon
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        lblLogo.setForeground(Theme.PRIMARY);
        sbHeader.add(lblLogo);

        JLabel lblTitle = new JLabel("SIG GUDANG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Theme.FG_LIGHT);
        sbHeader.add(lblTitle);

        JPanel topSidebar = new JPanel();
        topSidebar.setOpaque(false);
        topSidebar.setLayout(new BoxLayout(topSidebar, BoxLayout.Y_AXIS));
        topSidebar.add(sbHeader);
        topSidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sidebar Navigation Links Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create Views and Navigation Items
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Standard Panels
        PanelDashboard panelDashboard = new PanelDashboard();
        viewPanels.put("Dashboard", panelDashboard);
        contentPanel.add(panelDashboard, "Dashboard");

        PanelBarang panelBarang = new PanelBarang(currentUser);
        viewPanels.put("Master Barang", panelBarang);
        contentPanel.add(panelBarang, "Master Barang");

        PanelBarangMasuk panelMasuk = new PanelBarangMasuk(currentUser);
        viewPanels.put("Barang Masuk", panelMasuk);
        contentPanel.add(panelMasuk, "Barang Masuk");

        PanelBarangKeluar panelKeluar = new PanelBarangKeluar(currentUser);
        viewPanels.put("Barang Keluar", panelKeluar);
        contentPanel.add(panelKeluar, "Barang Keluar");

        PanelPengajuan panelPengajuan = new PanelPengajuan(currentUser);
        viewPanels.put("Pengajuan", panelPengajuan);
        contentPanel.add(panelPengajuan, "Pengajuan");

        PanelLaporan panelLaporan = new PanelLaporan(currentUser);
        viewPanels.put("Laporan", panelLaporan);
        contentPanel.add(panelLaporan, "Laporan");

        // Menu creation filtered by authorization
        addMenuItem(menuPanel, "Dashboard", "dashboard");
        addMenuItem(menuPanel, "Master Barang", "barang");
        addMenuItem(menuPanel, "Barang Masuk", "masuk");
        addMenuItem(menuPanel, "Barang Keluar", "keluar");
        addMenuItem(menuPanel, "Pengajuan", "pengajuan");
        addMenuItem(menuPanel, "Laporan", "laporan");

        if ("Admin Sistem".equalsIgnoreCase(currentUser.getRole())) {
            PanelUser panelUser = new PanelUser(currentUser);
            viewPanels.put("Kelola User", panelUser);
            contentPanel.add(panelUser, "Kelola User");
            addMenuItem(menuPanel, "Kelola User", "user");
        }

        topSidebar.add(menuPanel);
        sidebarPanel.add(topSidebar, BorderLayout.NORTH);

        // Sidebar Footer (User Info card and Logout)
        JPanel sbFooter = new JPanel(new BorderLayout());
        sbFooter.setOpaque(false);
        sbFooter.setBorder(new EmptyBorder(15, 15, 15, 15));

        // User info box
        JPanel userCard = new JPanel(new GridBagLayout());
        userCard.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblUserName = new JLabel(currentUser.getNamaUser());
        lblUserName.setFont(Theme.FONT_HEADER);
        lblUserName.setForeground(Theme.FG_LIGHT);
        
        JLabel lblUserRole = new JLabel(currentUser.getRole());
        lblUserRole.setFont(Theme.FONT_SMALL);
        lblUserRole.setForeground(Theme.PRIMARY);

        gbc.gridx = 0; gbc.gridy = 0;
        userCard.add(lblUserName, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        userCard.add(lblUserRole, gbc);

        // Logout Button
        JButton btnLogout = new JButton("Keluar");
        Theme.styleButton(btnLogout, Theme.DANGER, Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(80, 32));
        btnLogout.setFont(Theme.FONT_SMALL_BOLD);
        btnLogout.addActionListener(e -> handleLogout());

        sbFooter.add(userCard, BorderLayout.CENTER);
        sbFooter.add(btnLogout, BorderLayout.EAST);
        
        sidebarPanel.add(sbFooter, BorderLayout.SOUTH);

        mainPane.add(sidebarPanel, BorderLayout.WEST);
        mainPane.add(contentPanel, BorderLayout.CENTER);

        // Activate "Dashboard" as default menu
        Component[] items = menuPanel.getComponents();
        if (items.length > 0 && items[0] instanceof JPanel) {
            triggerMenuSelection((JPanel) items[0], "Dashboard");
        }
    }

    private void addMenuItem(JPanel container, String text, String iconType) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (activeMenuButton == this) {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (getBackground() == Theme.BG_CARD_ALT) {
                    g2.setColor(Theme.BG_CARD_ALT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setBackground(Theme.BG_SIDEBAR);
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Use custom Vector Icon component
        JLabel lblIcon = new JLabel(new MenuIcon(iconType));
        lblIcon.setPreferredSize(new Dimension(16, 16));
        lblIcon.setForeground(Theme.FG_LIGHT);
        btn.add(lblIcon);

        JLabel lblText = new JLabel(text);
        lblText.setFont(Theme.FONT_HEADER);
        lblText.setForeground(Theme.FG_LIGHT);
        btn.add(lblText);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeMenuButton != btn) {
                    btn.setBackground(Theme.BG_CARD_ALT);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeMenuButton != btn) {
                    btn.setBackground(Theme.BG_SIDEBAR);
                    btn.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                triggerMenuSelection(btn, text);
            }
        });

        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void triggerMenuSelection(JPanel btn, String text) {
        if (activeMenuButton != null) {
            JPanel prev = activeMenuButton;
            activeMenuButton = null;
            prev.setBackground(Theme.BG_SIDEBAR);
            prev.repaint();
        }
        
        activeMenuButton = btn;
        btn.repaint();

        // Switch card view
        cardLayout.show(contentPanel, text);

        // Dynamic refresh triggers when user navigates
        JPanel targetPanel = viewPanels.get(text);
        if (targetPanel instanceof PanelDashboard) {
            ((PanelDashboard) targetPanel).refreshData();
        }
    }

    public void refreshDashboardData() {
        JPanel dash = viewPanels.get("Dashboard");
        if (dash instanceof PanelDashboard) {
            ((PanelDashboard) dash).refreshData();
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin keluar dari aplikasi?", 
            "Konfirmasi Keluar", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            // Save main frame bounds first, then transition to Login Frame
            com.inventory.ui.theme.WindowState.saveState(MainFrame.this);
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    // --- Custom Vector Icon Class drawing clean icons using Java2D ---
    private static class MenuIcon implements Icon {
        private String type;
        private final int width = 16;
        private final int height = 16;

        public MenuIcon(String type) {
            this.type = type;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Icon color copies parent text foreground color (off-white by default)
            g2.setColor(c.getForeground());
            g2.setStroke(new BasicStroke(1.8f));

            if ("dashboard".equals(type)) {
                // Draw a 3-bar chart
                g2.fillRect(x + 1, y + 10, 3, 5);
                g2.fillRect(x + 6, y + 4, 3, 11);
                g2.fillRect(x + 11, y + 7, 3, 8);
            } else if ("barang".equals(type)) {
                // Draw a box icon
                g2.drawRect(x + 1, y + 4, 13, 10);
                g2.drawLine(x + 1, y + 4, x + 7, y + 1);
                g2.drawLine(x + 14, y + 4, x + 7, y + 1);
                g2.drawLine(x + 7, y + 4, x + 7, y + 13);
            } else if ("masuk".equals(type)) {
                // Draw a box with arrow pointing down
                g2.drawRect(x + 1, y + 7, 13, 8);
                // Down arrow
                g2.drawLine(x + 7, y, x + 7, y + 9);
                g2.drawLine(x + 4, y + 6, x + 7, y + 9);
                g2.drawLine(x + 10, y + 6, x + 7, y + 9);
            } else if ("keluar".equals(type)) {
                // Draw a box with arrow pointing up
                g2.drawRect(x + 1, y + 7, 13, 8);
                // Up arrow
                g2.drawLine(x + 7, y + 10, x + 7, y + 1);
                g2.drawLine(x + 4, y + 4, x + 7, y + 1);
                g2.drawLine(x + 10, y + 4, x + 7, y + 1);
            } else if ("pengajuan".equals(type)) {
                // Draw a checklist/paper icon
                g2.drawRect(x + 2, y + 1, 11, 14);
                g2.drawLine(x + 5, y + 5, x + 10, y + 5);
                g2.drawLine(x + 5, y + 8, x + 10, y + 8);
                g2.drawLine(x + 5, y + 11, x + 8, y + 11);
            } else if ("laporan".equals(type)) {
                // Draw a folder icon
                g2.drawRoundRect(x + 1, y + 3, 13, 11, 2, 2);
                g2.fillRect(x + 2, y + 1, 5, 3);
            } else if ("user".equals(type)) {
                // Draw user icon (head and shoulder outline)
                g2.drawOval(x + 5, y + 1, 6, 6);
                g2.drawArc(x + 1, y + 9, 14, 10, 0, 180);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
