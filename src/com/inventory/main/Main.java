package com.inventory.main;

import com.inventory.database.DatabaseHelper;
import com.inventory.ui.LoginFrame;
import com.inventory.ui.theme.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize SQLite Database Tables & Default Records
        DatabaseHelper.initializeDatabase();

        // 2. Set Up Modern Dark Theme Swing Look & Feel
        Theme.setupFlatLaf();

        // 3. Launch UI on Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
