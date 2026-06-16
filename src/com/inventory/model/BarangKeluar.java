package com.inventory.model;

public class BarangKeluar {
    private String idBarangKeluar;
    private String tanggalKeluar;
    private String idBarang;
    private String namaBarang; // UI convenience
    private int jumlahKeluar;
    private String penerima;

    public BarangKeluar() {}

    public BarangKeluar(String idBarangKeluar, String tanggalKeluar, String idBarang, String namaBarang, int jumlahKeluar, String penerima) {
        this.idBarangKeluar = idBarangKeluar;
        this.tanggalKeluar = tanggalKeluar;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.jumlahKeluar = jumlahKeluar;
        this.penerima = penerima;
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

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }
}
