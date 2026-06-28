package com.inventory.model;

public class BarangKeluar {
    private String idBarangKeluar;
    private String tanggalKeluar;
    private String idBarang;
    private String namaBarang; // UI convenience
    private int jumlahKeluar;
    private String idPembeli;
    private double hargaJual;

    public BarangKeluar() {}

    public BarangKeluar(String idBarangKeluar, String tanggalKeluar, String idBarang, String namaBarang, int jumlahKeluar, String idPembeli, double hargaJual) {
        this.idBarangKeluar = idBarangKeluar;
        this.tanggalKeluar = tanggalKeluar;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlahKeluar = jumlahKeluar;
        this.idPembeli = idPembeli;
        this.hargaJual = hargaJual;
    }

    public String getIdBarangKeluar() {
        return idBarangKeluar;
    }

    public void setIdBarangKeluar(String idBarangKeluar) {
        this.idBarangKeluar = idBarangKeluar;
    }

    public String getTanggalKeluar() {
        return tanggalKeluar;
    }

    public void setTanggalKeluar(String tanggalKeluar) {
        this.tanggalKeluar = tanggalKeluar;
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

    public int getJumlahKeluar() {
        return jumlahKeluar;
    }

    public void setJumlahKeluar(int jumlahKeluar) {
        this.jumlahKeluar = jumlahKeluar;
    }

    public String getIdPembeli() {
        return idPembeli;
    }

    public void setIdPembeli(String idPembeli) {
        this.idPembeli = idPembeli;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    // For backwards compatibility
    public String getPenerima() {
        return idPembeli;
    }

    public void setPenerima(String penerima) {
        this.idPembeli = penerima;
    }
}
