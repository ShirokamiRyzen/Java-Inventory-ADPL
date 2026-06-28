package com.inventory.model;

public class Supplier {
    private String idSupplier;
    private String nomorTelepon;

    public Supplier() {}

    public Supplier(String idSupplier, String nomorTelepon) {
        this.idSupplier = idSupplier;
        this.nomorTelepon = nomorTelepon;
    }

    public String getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(String idSupplier) {
        this.idSupplier = idSupplier;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }
}
