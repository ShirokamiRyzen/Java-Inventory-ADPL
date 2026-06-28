package com.inventory.model;

public class Barang {
    private String idBarang;
    private String namaBarang;
    private double hargaBeli;
    private double hargaJual;
    private String kategori;
    private int stok;

    public Barang() {}

    public Barang(String idBarang, String namaBarang, double hargaBeli, double hargaJual, String kategori, int stok) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.kategori = kategori;
        this.stok = stok;
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

    public double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    // For backwards compatibility
    public double getHarga() {
        return hargaJual;
    }
}
