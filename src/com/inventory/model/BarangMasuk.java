package com.inventory.model;

public class BarangMasuk {
    private String idBarangMasuk;
    private String tanggalMasuk;
    private String idBarang;
    private String namaBarang; // UI convenience
    private int jumlahMasuk;
    private String idSupplier;
    private double hargaBeli;

    public BarangMasuk() {}

    public BarangMasuk(String idBarangMasuk, String tanggalMasuk, String idBarang, String namaBarang, int jumlahMasuk, String idSupplier, double hargaBeli) {
        this.idBarangMasuk = idBarangMasuk;
        this.tanggalMasuk = tanggalMasuk;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlahMasuk = jumlahMasuk;
        this.idSupplier = idSupplier;
        this.hargaBeli = hargaBeli;
    }

    public String getIdBarangMasuk() {
        return idBarangMasuk;
    }

    public void setIdBarangMasuk(String idBarangMasuk) {
        this.idBarangMasuk = idBarangMasuk;
    }

    public String getTanggalMasuk() {
        return tanggalMasuk;
    }

    public void setTanggalMasuk(String tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getJumlahMasuk() {
        return jumlahMasuk;
    }

    public void setJumlahMasuk(int jumlahMasuk) {
        this.jumlahMasuk = jumlahMasuk;
    }

    public String getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(String idSupplier) {
        this.idSupplier = idSupplier;
    }

    public double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    // For backwards compatibility
    public String getSupplier() {
        return idSupplier;
    }

    public void setSupplier(String supplier) {
        this.idSupplier = supplier;
    }
}
