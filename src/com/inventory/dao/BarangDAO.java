package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.Barang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public List<Barang> getAllBarang() {
        List<Barang> list = new ArrayList<>();
        String query = "SELECT * FROM barang ORDER BY id_barang ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new Barang(
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getDouble("harga"),
                    rs.getString("kategori"),
                    rs.getInt("stok")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Barang getBarangById(String idBarang) {
        String query = "SELECT * FROM barang WHERE id_barang = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, idBarang);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Barang(
                        rs.getString("id_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("harga"),
                        rs.getString("kategori"),
                        rs.getInt("stok")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertBarang(Barang barang) {
        String query = "INSERT INTO barang (id_barang, nama_barang, harga, kategori, stok) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, barang.getIdBarang());
            pstmt.setString(2, barang.getNamaBarang());
            pstmt.setDouble(3, barang.getHarga());
            pstmt.setString(4, barang.getKategori());
            pstmt.setInt(5, barang.getStok());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBarang(Barang barang) {
        String query = "UPDATE barang SET nama_barang = ?, harga = ?, kategori = ?, stok = ? WHERE id_barang = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setDouble(2, barang.getHarga());
            pstmt.setString(3, barang.getKategori());
            pstmt.setInt(4, barang.getStok());
            pstmt.setString(5, barang.getIdBarang());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBarang(String idBarang) {
        String query = "DELETE FROM barang WHERE id_barang = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, idBarang);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStok(String idBarang, int change) {
        String query = "UPDATE barang SET stok = stok + ? WHERE id_barang = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, change);
            pstmt.setString(2, idBarang);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNewId() {
        String query = "SELECT id_barang FROM barang ORDER BY id_barang DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id_barang"); // e.g. "BRG003"
                if (lastId.startsWith("BRG")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(3));
                        return String.format("BRG%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and generate default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "BRG001";
    }
}
