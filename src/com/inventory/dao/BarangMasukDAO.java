package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.BarangMasuk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangMasukDAO {

    public List<BarangMasuk> getAllBarangMasuk() {
        List<BarangMasuk> list = new ArrayList<>();
        String query = "SELECT bm.*, b.nama_barang FROM barang_masuk bm " +
                       "JOIN barang b ON bm.id_barang = b.id_barang " +
                       "ORDER BY bm.id_barang_masuk DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new BarangMasuk(
                    rs.getString("id_barang_masuk"),
                    rs.getString("tanggal_masuk"),
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_masuk"),
                    rs.getString("supplier")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBarangMasuk(BarangMasuk bm) {
        String queryInsert = "INSERT INTO barang_masuk (id_barang_masuk, tanggal_masuk, id_barang, jumlah_masuk, supplier) VALUES (?, ?, ?, ?, ?)";
        String queryUpdateStock = "UPDATE barang SET stok = stok + ? WHERE id_barang = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // start transaction
            
            // 1. Insert incoming goods record
            try (PreparedStatement pstmtInsert = conn.prepareStatement(queryInsert)) {
                pstmtInsert.setString(1, bm.getIdBarangMasuk());
                pstmtInsert.setString(2, bm.getTanggalMasuk());
                pstmtInsert.setString(3, bm.getIdBarang());
                pstmtInsert.setInt(4, bm.getJumlahMasuk());
                pstmtInsert.setString(5, bm.getSupplier());
                pstmtInsert.executeUpdate();
            }
            
            // 2. Update stock of item
            try (PreparedStatement pstmtUpdateStock = conn.prepareStatement(queryUpdateStock)) {
                pstmtUpdateStock.setInt(1, bm.getJumlahMasuk());
                pstmtUpdateStock.setString(2, bm.getIdBarang());
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
        String query = "SELECT id_barang_masuk FROM barang_masuk ORDER BY id_barang_masuk DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id_barang_masuk"); // e.g. "BM003"
                if (lastId.startsWith("BM")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(2));
                        return String.format("BM%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and generate default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "BM001";
    }
}
