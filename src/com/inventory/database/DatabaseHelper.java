package com.inventory.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHelper {
    private static final String CONFIG_FILE = "db.properties";
    private static String dbHost = "localhost";
    private static String dbPort = "3306";
    private static String dbName = "inventory_db";
    private static String dbUsername = "root";
    private static String dbPassword = "";
    private static String dbUrl;
    private static boolean configLoaded = false;

    public static synchronized void loadConfig() {
        if (configLoaded) return;

        Properties props = new Properties();
        File file = new File(CONFIG_FILE);

        if (!file.exists()) {
            props.setProperty("db.host", "localhost");
            props.setProperty("db.port", "3306");
            props.setProperty("db.name", "inventory_db");
            props.setProperty("db.username", "root");
            props.setProperty("db.password", "");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                props.store(fos, "Database Connection Configuration\nChange these values to point to your MariaDB server (local or cloud)");
                System.out.println("Default database configuration created at: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Warning: Could not save default db.properties: " + e.getMessage());
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println("Warning: Could not load db.properties, using default connection settings: " + e.getMessage());
            }
        }

        dbHost = props.getProperty("db.host", "localhost").trim();
        dbPort = props.getProperty("db.port", "3306").trim();
        dbName = props.getProperty("db.name", "inventory_db").trim();
        dbUsername = props.getProperty("db.username", "root").trim();
        dbPassword = props.getProperty("db.password", "");

        // createDatabaseIfNotExist=true ensures the database schema is auto-created if it doesn't exist
        dbUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbName + "?createDatabaseIfNotExist=true";
        configLoaded = true;
    }

    public static Connection getConnection() throws SQLException {
        loadConfig();
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found in classpath.", e);
        }
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public static void resetDatabaseSchema() throws SQLException {
        loadConfig();
        // Connect to MariaDB without database name to drop the database
        String serverUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/";
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found in classpath.", e);
        }
        try (Connection conn = DriverManager.getConnection(serverUrl, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP DATABASE IF EXISTS " + dbName);
            System.out.println("Dropped MariaDB database '" + dbName + "' successfully.");
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id_user INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(50) NOT NULL CHECK(role IN ('Admin Sistem', 'Admin Gudang', 'Pemilik'))," +
                    "nama_user VARCHAR(100) NOT NULL" +
                    ");");

            // 2. Create Supplier table
            stmt.execute("CREATE TABLE IF NOT EXISTS supplier (" +
                    "id_supplier VARCHAR(50) PRIMARY KEY," +
                    "nomor_telepon VARCHAR(50) NOT NULL" +
                    ");");

            // 3. Create Barang table
            stmt.execute("CREATE TABLE IF NOT EXISTS barang (" +
                    "id_barang VARCHAR(50) PRIMARY KEY," +
                    "nama_barang VARCHAR(100) NOT NULL," +
                    "harga_beli DOUBLE NOT NULL CHECK(harga_beli >= 0)," +
                    "harga_jual DOUBLE NOT NULL CHECK(harga_jual >= 0)," +
                    "kategori VARCHAR(50) NOT NULL," +
                    "stok INT NOT NULL DEFAULT 0 CHECK(stok >= 0)" +
                    ");");

            // 4. Create Barang Masuk table
            stmt.execute("CREATE TABLE IF NOT EXISTS barang_masuk (" +
                    "id_barang_masuk VARCHAR(50) PRIMARY KEY," +
                    "tanggal_masuk VARCHAR(50) NOT NULL," +
                    "id_barang VARCHAR(50) NOT NULL," +
                    "jumlah_masuk INT NOT NULL CHECK(jumlah_masuk > 0)," +
                    "id_supplier VARCHAR(50) NOT NULL," +
                    "harga_beli DOUBLE NOT NULL CHECK(harga_beli >= 0)," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE," +
                    "FOREIGN KEY (id_supplier) REFERENCES supplier(id_supplier) ON DELETE CASCADE" +
                    ");");

            // 5. Create Barang Keluar table
            stmt.execute("CREATE TABLE IF NOT EXISTS barang_keluar (" +
                    "id_barang_keluar VARCHAR(50) PRIMARY KEY," +
                    "tanggal_keluar VARCHAR(50) NOT NULL," +
                    "id_barang VARCHAR(50) NOT NULL," +
                    "jumlah_keluar INT NOT NULL CHECK(jumlah_keluar > 0)," +
                    "id_pembeli VARCHAR(100) NOT NULL," +
                    "harga_jual DOUBLE NOT NULL CHECK(harga_jual >= 0)," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE" +
                    ");");

            // 6. Create Pengajuan Pembelian table
            stmt.execute("CREATE TABLE IF NOT EXISTS pengajuan_pembelian (" +
                    "id_pengajuan VARCHAR(50) PRIMARY KEY," +
                    "id_barang VARCHAR(50) NOT NULL," +
                    "tanggal_pengajuan VARCHAR(50) NOT NULL," +
                    "jumlah_pengajuan INT NOT NULL CHECK(jumlah_pengajuan > 0)," +
                    "status_pengajuan VARCHAR(50) NOT NULL DEFAULT 'Pending' CHECK(status_pengajuan IN ('Pending', 'Disetujui', 'Ditolak'))," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE" +
                    ");");

            // 7. Create Laporan table
            stmt.execute("CREATE TABLE IF NOT EXISTS laporan (" +
                    "id_laporan VARCHAR(50) PRIMARY KEY," +
                    "tanggal_laporan VARCHAR(50) NOT NULL," +
                    "jenis_laporan VARCHAR(100) NOT NULL," +
                    "keterangan TEXT" +
                    ");");

            // Insert default users if table is empty
            String checkUsers = "SELECT COUNT(*) FROM users";
            try (ResultSet rs = stmt.executeQuery(checkUsers)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertDefaultUser(conn, "admin", "admin123", "Admin Sistem", "Administrator");
                    insertDefaultUser(conn, "gudang", "gudang123", "Admin Gudang", "Staf Gudang");
                    insertDefaultUser(conn, "pemilik", "pemilik123", "Pemilik", "Bapak Owner");
                    System.out.println("Default users created successfully.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static void insertDefaultUser(Connection conn, String username, String password, String role, String namaUser) throws SQLException {
        String query = "INSERT INTO users (username, password, role, nama_user) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, role);
            pstmt.setString(4, namaUser);
            pstmt.executeUpdate();
        }
    }
}
