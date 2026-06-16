package com.inventory.ui.theme;

import java.awt.Rectangle;
import java.io.*;
import java.util.Properties;
import javax.swing.JFrame;

public class WindowState {
    private static final String CONFIG_FILE = "window.properties";

    /**
     * Saves the current window size, coordinates, and maximized state to a properties file.
     */
    public static void saveState(JFrame frame) {
        Properties props = new Properties();
        Rectangle bounds = frame.getBounds();
        int state = frame.getExtendedState();
        boolean isMaximized = (state & JFrame.MAXIMIZED_BOTH) != 0;

        props.setProperty("x", String.valueOf(bounds.x));
        props.setProperty("y", String.valueOf(bounds.y));
        props.setProperty("width", String.valueOf(bounds.width));
        props.setProperty("height", String.valueOf(bounds.height));
        props.setProperty("maximized", String.valueOf(isMaximized));

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Sistem Inventaris Gudang - Window State Settings");
        } catch (IOException e) {
            // Silently ignore configuration save errors
        }
    }

    /**
     * Loads the last saved window state. Fallbacks to default values if config is missing or corrupted.
     */
    public static void loadState(JFrame frame, int defaultWidth, int defaultHeight) {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);
        
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
                int x = Integer.parseInt(props.getProperty("x"));
                int y = Integer.parseInt(props.getProperty("y"));
                int w = Integer.parseInt(props.getProperty("width"));
                int h = Integer.parseInt(props.getProperty("height"));
                boolean max = Boolean.parseBoolean(props.getProperty("maximized"));

                // Apply saved bounds
                frame.setBounds(x, y, w, h);
                
                // If it was maximized, apply maximized state
                if (max) {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                return;
            } catch (Exception e) {
                // If parsing fails, fall through to default sizing
            }
        }

        // Fallback default sizes centered on screen
        frame.setSize(defaultWidth, defaultHeight);
        frame.setLocationRelativeTo(null);
    }
}
