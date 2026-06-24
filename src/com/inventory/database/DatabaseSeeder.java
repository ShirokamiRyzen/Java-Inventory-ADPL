package com.inventory.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void main(String[] args) {
        System.out.println("Starting database seeding process...");
        
        // Ensure tables exist
        DatabaseHelper.initializeDatabase();

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Disable foreign key constraints temporarily to clear data cleanly
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                
                // Clear existing records except users (we keep testing accounts)
                stmt.execute("DELETE FROM barang;");
                stmt.execute("DELETE FROM barang_masuk;");
                stmt.execute("DELETE FROM barang_keluar;");
                stmt.execute("DELETE FROM pengajuan_pembelian;");
                stmt.execute("DELETE FROM laporan;");
                
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }

            System.out.println("Existing transaction and product data cleared.");

            // 1. Seed Barang (Master Sembako)
            // Format: ID, Nama, Harga, Kategori, Stok
            insertBarang(conn, "BRG001", "Beras Pandan Wangi 5kg", 72000.0, "Bahan Pokok", 45);
            insertBarang(conn, "BRG002", "Minyak Goreng Bimoli 2L", 36500.0, "Minyak & Lemak", 60);
            insertBarang(conn, "BRG003", "Gula Pasir Gulaku 1kg", 16000.0, "Bahan Pokok", 80);
            insertBarang(conn, "BRG004", "Telur Ayam Negeri 1kg", 28000.0, "Bahan Pokok", 120);
            insertBarang(conn, "BRG005", "Tepung Terigu Segitiga Biru 1kg", 12500.0, "Bahan Pokok", 7); // Low stock
            insertBarang(conn, "BRG006", "Kecap Manis Bango 550ml", 21000.0, "Bumbu Dapur", 30);
            insertBarang(conn, "BRG007", "Indomie Goreng Spesial 1 Dus", 112000.0, "Bahan Pokok", 15);
            insertBarang(conn, "BRG008", "Garam Dapur Cap Kapal 250g", 2500.0, "Bumbu Dapur", 5);  // Low stock
            insertBarang(conn, "BRG009", "Teh Celup Sariwangi 25s", 6500.0, "Minuman", 50);
            insertBarang(conn, "BRG010", "Sabun Mandi Lifebuoy 85g", 4000.0, "Sabun & Pembersih", 15);
            insertBarang(conn, "BRG011", "Rinso Liquid Deterjen 750ml", 19500.0, "Sabun & Pembersih", 4); // Low stock
            insertBarang(conn, "BRG012", "Kopi Kapal Api Spesial 165g", 14200.0, "Minuman", 25);
            System.out.println("Seeded 12 master products (sembako).");

            // 2. Seed Barang Masuk (Restock logs)
            // Format: ID Masuk, Tanggal, ID Barang, Qty, Supplier
            insertBarangMasuk(conn, "BM001", "2026-06-10", "BRG001", 50, "PT Roda Mas Cemerlang");
            insertBarangMasuk(conn, "BM002", "2026-06-11", "BRG002", 80, "PT Bimoli Food");
            insertBarangMasuk(conn, "BM003", "2026-06-12", "BRG003", 100, "Bulog Divre Jabar");
            insertBarangMasuk(conn, "BM004", "2026-06-13", "BRG007", 20, "PT Indofood Distribusi");
            System.out.println("Seeded 4 incoming goods records.");

            // 3. Seed Barang Keluar (Distribution logs)
            // Format: ID Keluar, Tanggal, ID Barang, Qty, Penerima
            insertBarangKeluar(conn, "BK001", "2026-06-14", "BRG001", 5, "Toko Kelontong Sinar");
            insertBarangKeluar(conn, "BK002", "2026-06-14", "BRG002", 20, "Warung Nasi Bu Joko");
            insertBarangKeluar(conn, "BK003", "2026-06-15", "BRG007", 5, "Koperasi Maju Jaya");
            System.out.println("Seeded 3 outgoing goods records.");

            // 4. Seed Pengajuan Pembelian (Purchase requests)
            // Format: ID Pengajuan, ID Barang, Tanggal, Qty, Status
            insertPengajuan(conn, "PP001", "BRG005", "2026-06-15", 30, "Pending"); // For testing owner approval
            insertPengajuan(conn, "PP002", "BRG008", "2026-06-15", 50, "Pending"); // For testing owner approval
            insertPengajuan(conn, "PP003", "BRG011", "2026-06-14", 25, "Disetujui");
            System.out.println("Seeded 3 purchase requests.");

            System.out.println("Database seeding completed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database seeding failed: " + e.getMessage());
        }
    }

    private static void insertBarang(Connection conn, String id, String nama, double harga, String kategori, int stok) throws SQLException {
        String query = "INSERT INTO barang (id_barang, nama_barang, harga, kategori, stok) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, nama);
            pstmt.setDouble(3, harga);
            pstmt.setString(4, kategori);
            pstmt.setInt(5, stok);
            pstmt.executeUpdate();
        }
    }

    private static void insertBarangMasuk(Connection conn, String id, String tgl, String idBarang, int qty, String supplier) throws SQLException {
        String query = "INSERT INTO barang_masuk (id_barang_masuk, tanggal_masuk, id_barang, jumlah_masuk, supplier) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, tgl);
            pstmt.setString(3, idBarang);
            pstmt.setInt(4, qty);
            pstmt.setString(5, supplier);
            pstmt.executeUpdate();
        }
    }

    private static void insertBarangKeluar(Connection conn, String id, String tgl, String idBarang, int qty, String penerima) throws SQLException {
        String query = "INSERT INTO barang_keluar (id_barang_keluar, tanggal_keluar, id_barang, jumlah_keluar, penerima) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, tgl);
            pstmt.setString(3, idBarang);
            pstmt.setInt(4, qty);
            pstmt.setString(5, penerima);
            pstmt.executeUpdate();
        }
    }

    private static void insertPengajuan(Connection conn, String id, String idBarang, String tgl, int qty, String status) throws SQLException {
        String query = "INSERT INTO pengajuan_pembelian (id_pengajuan, id_barang, tanggal_pengajuan, jumlah_pengajuan, status_pengajuan) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, idBarang);
            pstmt.setString(3, tgl);
            pstmt.setInt(4, qty);
            pstmt.setString(5, status);
            pstmt.executeUpdate();
        }
    }
}
