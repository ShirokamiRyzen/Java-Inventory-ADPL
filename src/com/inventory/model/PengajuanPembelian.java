package com.inventory.model;

public class PengajuanPembelian {
    private String idPengajuan;
    private String idBarang;
    private String namaBarang; // UI convenience
    private String tanggalPengajuan;
    private int jumlahPengajuan;
    private String statusPengajuan; // 'Pending', 'Disetujui', 'Ditolak'

    public PengajuanPembelian() {}

    public PengajuanPembelian(String idPengajuan, String idBarang, String namaBarang, String tanggalPengajuan, int jumlahPengajuan, String statusPengajuan) {
        this.idPengajuan = idPengajuan;
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.tanggalPengajuan = tanggalPengajuan;
        this.jumlahPengajuan = jumlahPengajuan;
        this.statusPengajuan = statusPengajuan;
    }

    public String getIdPengajuan() {
        return idPengajuan;
    }

    public void setIdPengajuan(String idPengajuan) {
        this.idPengajuan = idPengajuan;
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

    public String getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(String tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public int getJumlahPengajuan() {
        return jumlahPengajuan;
    }

    public void setJumlahPengajuan(int jumlahPengajuan) {
        this.jumlahPengajuan = jumlahPengajuan;
    }

    public String getStatusPengajuan() {
        return statusPengajuan;
    }

    public void setStatusPengajuan(String statusPengajuan) {
        this.statusPengajuan = statusPengajuan;
    }
}
