package com.inventory.ui;

import com.inventory.dao.UserDAO;
import com.inventory.model.User;
import com.inventory.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Log In - Sistem Inventaris Gudang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        com.inventory.ui.theme.WindowState.loadState(this, 900, 600);

        // Save window bounds on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                com.inventory.ui.theme.WindowState.saveState(LoginFrame.this);
            }
        });

        // Main Background Panel with Gradient
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Radial/Linear Gradient for sleek premium background
                GradientPaint gp = new GradientPaint(
                    0, 0, Theme.BG_DARK, 
                    getWidth(), getHeight(), new Color(27, 20, 58) // Deep indigo mix
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw decorative abstract glowing circles
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 15));
                g2.fillOval(-100, -100, 400, 400);
                g2.fillOval(getWidth() - 250, getHeight() - 250, 400, 400);
                
                g2.dispose();
            }
        };
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        // Credentials Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                // Accent top bar
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), 8, 24, 24);
                g2.fillRect(0, 4, getWidth(), 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 440));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Logo / Icon (Custom Drawn Box)
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Circle bg
                g2.setColor(new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 30));
                g2.fillOval(0, 0, 60, 60);
                
                // Box icon outline
                g2.setColor(Theme.PRIMARY);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(16, 20, 28, 22);
                g2.drawLine(16, 20, 30, 12);
                g2.drawLine(44, 20, 30, 12);
                g2.drawLine(30, 20, 30, 42);
                
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(60, 60));
        logoPanel.setMaximumSize(new Dimension(60, 60));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App Title
        JLabel lblTitle = new JLabel("INVENTORY APP");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.FG_LIGHT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistem Manajemen Gudang");
        lblSubtitle.setFont(Theme.FONT_SMALL);
        lblSubtitle.setForeground(Theme.FG_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Fields setup
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(Theme.FONT_SMALL_BOLD);
        lblUser.setForeground(Theme.FG_MUTED);

        txtUsername = new JTextField();
        txtUsername.setFont(Theme.FONT_BODY);
        txtUsername.setBackground(Theme.BG_DARK);
        txtUsername.setForeground(Theme.FG_LIGHT);
        txtUsername.setCaretColor(Theme.FG_LIGHT);
        txtUsername.setMaximumSize(new Dimension(310, 40));
        txtUsername.setPreferredSize(new Dimension(310, 40));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username...");

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(Theme.FONT_SMALL_BOLD);
        lblPass.setForeground(Theme.FG_MUTED);

        txtPassword = new JPasswordField();
        txtPassword.setFont(Theme.FONT_BODY);
        txtPassword.setBackground(Theme.BG_DARK);
        txtPassword.setForeground(Theme.FG_LIGHT);
        txtPassword.setCaretColor(Theme.FG_LIGHT);
        txtPassword.setMaximumSize(new Dimension(310, 40));
        txtPassword.setPreferredSize(new Dimension(310, 40));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password...");

        // Login Button
        btnLogin = new JButton("LOG IN");
        Theme.styleButton(btnLogin, Theme.PRIMARY, Color.WHITE);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(310, 45));
        btnLogin.setPreferredSize(new Dimension(310, 45));

        // Hover Effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(Theme.PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(Theme.PRIMARY);
            }
        });

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(Theme.FONT_SMALL);
        lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add action listener to login button
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Add enter key trigger for login
        ActionListener enterAction = e -> handleLogin();
        txtUsername.addActionListener(enterAction);
        txtPassword.addActionListener(enterAction);

        // Nest input fields in a sub-panel to maintain left-alignment
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(310, 135));
        formPanel.setPreferredSize(new Dimension(310, 135));

        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(lblUser);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(lblPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(txtPassword);

        // Add components to card (using perfect center alignment)
        card.add(logoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblTitle);
        card.add(lblSubtitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblError);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(btnLogin);

        // Add card to background
        bgPanel.add(card);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan password tidak boleh kosong!");
            return;
        }

        btnLogin.setEnabled(false);
        lblError.setText("Memverifikasi...");

        // Perform login verification in background worker to prevent UI freezing
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userDAO.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        lblError.setText(" ");
                        // Save login frame bounds first, then transition to Main Frame
                        com.inventory.ui.theme.WindowState.saveState(LoginFrame.this);
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(user).setVisible(true);
                        });
                    } else {
                        lblError.setText("Username atau password salah!");
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    lblError.setText("Terjadi kesalahan sistem!");
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
