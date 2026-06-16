package com.inventory.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Enable Foreign Keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 1. Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id_user INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL CHECK(role IN ('Admin Sistem', 'Admin Gudang', 'Pemilik'))," +
                    "nama_user TEXT NOT NULL" +
                    ");");

            // 2. Create Barang table
            stmt.execute("CREATE TABLE IF NOT EXISTS barang (" +
                    "id_barang TEXT PRIMARY KEY," +
                    "nama_barang TEXT NOT NULL," +
                    "harga REAL NOT NULL CHECK(harga >= 0)," +
                    "kategori TEXT NOT NULL," +
                    "stok INTEGER NOT NULL DEFAULT 0 CHECK(stok >= 0)" +
                    ");");

            // 3. Create Barang Masuk table
            stmt.execute("CREATE TABLE IF NOT EXISTS barang_masuk (" +
                    "id_barang_masuk TEXT PRIMARY KEY," +
                    "tanggal_masuk TEXT NOT NULL," +
                    "id_barang TEXT NOT NULL," +
                    "jumlah_masuk INTEGER NOT NULL CHECK(jumlah_masuk > 0)," +
                    "supplier TEXT NOT NULL," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE" +
                    ");");

            // 4. Create Barang Keluar table (for completeness in inventory tracking)
            stmt.execute("CREATE TABLE IF NOT EXISTS barang_keluar (" +
                    "id_barang_keluar TEXT PRIMARY KEY," +
                    "tanggal_keluar TEXT NOT NULL," +
                    "id_barang TEXT NOT NULL," +
                    "jumlah_keluar INTEGER NOT NULL CHECK(jumlah_keluar > 0)," +
                    "penerima TEXT NOT NULL," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE" +
                    ");");

            // 5. Create Pengajuan Pembelian table
            stmt.execute("CREATE TABLE IF NOT EXISTS pengajuan_pembelian (" +
                    "id_pengajuan TEXT PRIMARY KEY," +
                    "id_barang TEXT NOT NULL," +
                    "tanggal_pengajuan TEXT NOT NULL," +
                    "jumlah_pengajuan INTEGER NOT NULL CHECK(jumlah_pengajuan > 0)," +
                    "status_pengajuan TEXT NOT NULL DEFAULT 'Pending' CHECK(status_pengajuan IN ('Pending', 'Disetujui', 'Ditolak'))," +
                    "FOREIGN KEY (id_barang) REFERENCES barang(id_barang) ON DELETE CASCADE" +
                    ");");

            // 6. Create Laporan table
            stmt.execute("CREATE TABLE IF NOT EXISTS laporan (" +
                    "id_laporan TEXT PRIMARY KEY," +
                    "tanggal_laporan TEXT NOT NULL," +
                    "jenis_laporan TEXT NOT NULL," +
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

            // No sample data seeded by default for fresh migration support

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void insertDefaultUser(Connection conn, String username, String password, String role, String namaUser) throws SQLException {
        String query = "INSERT INTO users (username, password, role, nama_user) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In production, hash this password!
            pstmt.setString(3, role);
            pstmt.setString(4, namaUser);
            pstmt.executeUpdate();
        }
    }
}
