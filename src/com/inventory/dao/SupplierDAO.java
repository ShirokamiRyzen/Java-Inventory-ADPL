package com.inventory.dao;

import com.inventory.database.DatabaseHelper;
import com.inventory.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> getAllSupplier() {
        List<Supplier> list = new ArrayList<>();
        String query = "SELECT * FROM supplier ORDER BY id_supplier ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new Supplier(
                    rs.getString("id_supplier"),
                    rs.getString("nomor_telepon")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Supplier getSupplierById(String idSupplier) {
        String query = "SELECT * FROM supplier WHERE id_supplier = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, idSupplier);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                        rs.getString("id_supplier"),
                        rs.getString("nomor_telepon")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertSupplier(Supplier supplier) {
        String query = "INSERT INTO supplier (id_supplier, nomor_telepon) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, supplier.getIdSupplier());
            pstmt.setString(2, supplier.getNomorTelepon());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String query = "UPDATE supplier SET nomor_telepon = ? WHERE id_supplier = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, supplier.getNomorTelepon());
            pstmt.setString(2, supplier.getIdSupplier());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(String idSupplier) {
        String query = "DELETE FROM supplier WHERE id_supplier = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, idSupplier);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
