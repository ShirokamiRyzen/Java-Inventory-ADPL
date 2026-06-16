package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.PengajuanPembelian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PengajuanPembelianDAO {

    public List<PengajuanPembelian> getAllPengajuan() {
        List<PengajuanPembelian> list = new ArrayList<>();
        String query = "SELECT pp.*, b.nama_barang FROM pengajuan_pembelian pp " +
                       "JOIN barang b ON pp.id_barang = b.id_barang " +
                       "ORDER BY pp.id_pengajuan DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new PengajuanPembelian(
                    rs.getString("id_pengajuan"),
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("tanggal_pengajuan"),
                    rs.getInt("jumlah_pengajuan"),
                    rs.getString("status_pengajuan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertPengajuan(PengajuanPembelian pp) {
        String query = "INSERT INTO pengajuan_pembelian (id_pengajuan, id_barang, tanggal_pengajuan, jumlah_pengajuan, status_pengajuan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, pp.getIdPengajuan());
            pstmt.setString(2, pp.getIdBarang());
            pstmt.setString(3, pp.getTanggalPengajuan());
            pstmt.setInt(4, pp.getJumlahPengajuan());
            pstmt.setString(5, pp.getStatusPengajuan());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(String idPengajuan, String status) {
        String query = "UPDATE pengajuan_pembelian SET status_pengajuan = ? WHERE id_pengajuan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, idPengajuan);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNewId() {
        String query = "SELECT id_pengajuan FROM pengajuan_pembelian ORDER BY id_pengajuan DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id_pengajuan"); // e.g. "PP003"
                if (lastId.startsWith("PP")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(2));
                        return String.format("PP%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and generate default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PP001";
    }
}
