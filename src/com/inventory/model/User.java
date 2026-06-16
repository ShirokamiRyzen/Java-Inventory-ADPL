package com.inventory.model;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String role;
    private String namaUser;

    public User() {}

    public User(int idUser, String username, String password, String role, String namaUser) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
        this.namaUser = namaUser;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}
