package com.inventory.model;

public class Laporan {
    private String idLaporan;
    private String tanggalLaporan;
    private String jenisLaporan; // e.g. "Stok", "Barang Masuk", "Bulanan"
    private String keterangan;

    public Laporan() {}

    public Laporan(String idLaporan, String tanggalLaporan, String jenisLaporan, String keterangan) {
        this.idLaporan = idLaporan;
        this.tanggalLaporan = tanggalLaporan;
        this.jenisLaporan = jenisLaporan;
        this.keterangan = keterangan;
    }

    public String getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(String idLaporan) {
        this.idLaporan = idLaporan;
    }

    public String getTanggalLaporan() {
        return tanggalLaporan;
    }

    public void setTanggalLaporan(String tanggalLaporan) {
        this.tanggalLaporan = tanggalLaporan;
    }

    public String getJenisLaporan() {
        return jenisLaporan;
    }

    public void setJenisLaporan(String jenisLaporan) {
        this.jenisLaporan = jenisLaporan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
