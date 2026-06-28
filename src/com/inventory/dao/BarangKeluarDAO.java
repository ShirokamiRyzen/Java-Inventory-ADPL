package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.BarangKeluar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangKeluarDAO {

    public List<BarangKeluar> getAllBarangKeluar() {
        List<BarangKeluar> list = new ArrayList<>();
        String query = "SELECT bk.*, b.nama_barang FROM barang_keluar bk " +
                       "JOIN barang b ON bk.id_barang = b.id_barang " +
                       "ORDER BY bk.id_barang_keluar DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new BarangKeluar(
                    rs.getString("id_barang_keluar"),
                    rs.getString("tanggal_keluar"),
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_keluar"),
                    rs.getString("id_pembeli"),
                    rs.getDouble("harga_jual")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBarangKeluar(BarangKeluar bk) {
        String queryCheckStock = "SELECT stok FROM barang WHERE id_barang = ?";
        String queryInsert = "INSERT INTO barang_keluar (id_barang_keluar, tanggal_keluar, id_barang, jumlah_keluar, id_pembeli, harga_jual) VALUES (?, ?, ?, ?, ?, ?)";
        String queryUpdateStock = "UPDATE barang SET stok = stok - ? WHERE id_barang = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // start transaction
            
            // 1. Check if stock is sufficient
            int currentStock = 0;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(queryCheckStock)) {
                pstmtCheck.setString(1, bk.getIdBarang());
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        currentStock = rs.getInt("stok");
                    } else {
                        throw new SQLException("Barang tidak ditemukan!");
                    }
                }
            }
            
            if (currentStock < bk.getJumlahKeluar()) {
                throw new SQLException("Stok tidak mencukupi! Stok saat ini: " + currentStock);
            }
            
            // 2. Insert record
            try (PreparedStatement pstmtInsert = conn.prepareStatement(queryInsert)) {
                pstmtInsert.setString(1, bk.getIdBarangKeluar());
                pstmtInsert.setString(2, bk.getTanggalKeluar());
                pstmtInsert.setString(3, bk.getIdBarang());
                pstmtInsert.setInt(4, bk.getJumlahKeluar());
                pstmtInsert.setString(5, bk.getIdPembeli());
                pstmtInsert.setDouble(6, bk.getHargaJual());
                pstmtInsert.executeUpdate();
            }
            
            // 3. Update stock (subtract)
            try (PreparedStatement pstmtUpdateStock = conn.prepareStatement(queryUpdateStock)) {
                pstmtUpdateStock.setInt(1, bk.getJumlahKeluar());
                pstmtUpdateStock.setString(2, bk.getIdBarang());
                pstmtUpdateStock.executeUpdate();
            }
            
            conn.commit(); // commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String generateNewId() {
        String query = "SELECT id_barang_keluar FROM barang_keluar ORDER BY id_barang_keluar DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id_barang_keluar"); // e.g. "BK003"
                if (lastId.startsWith("BK")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(2));
                        return String.format("BK%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and generate default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "BK001";
    }
}
