package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.Laporan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    public List<Laporan> getAllLaporan() {
        List<Laporan> list = new ArrayList<>();
        String query = "SELECT * FROM laporan ORDER BY id_laporan DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new Laporan(
                    rs.getString("id_laporan"),
                    rs.getString("tanggal_laporan"),
                    rs.getString("jenis_laporan"),
                    rs.getString("keterangan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertLaporan(Laporan lap) {
        String query = "INSERT INTO laporan (id_laporan, tanggal_laporan, jenis_laporan, keterangan) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, lap.getIdLaporan());
            pstmt.setString(2, lap.getTanggalLaporan());
            pstmt.setString(3, lap.getJenisLaporan());
            pstmt.setString(4, lap.getKeterangan());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNewId() {
        String query = "SELECT id_laporan FROM laporan ORDER BY id_laporan DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                String lastId = rs.getString("id_laporan"); // e.g. "LAP003"
                if (lastId.startsWith("LAP")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(3));
                        return String.format("LAP%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and generate default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "LAP001";
    }
}
