package br.com.voltz.model;

import java.time.LocalDateTime;

import com.google.gson.annotations.JsonAdapter;

import br.com.voltz.adapter.LocalDateTimeAdapter;
import br.com.voltz.util.ValidationUtil;

public class Users {
    private int id;
    private String userName;
    private String cpfCnpj;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean active;

    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime dateCreated;

    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime dateUpdated;

    public Users(String userName, String cpfCnpj, String email, String phoneNumber, String password, boolean active) {
        this.userName = userName;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.active = active;
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
    }

    public Users() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void updateDate() {
        this.dateUpdated = LocalDateTime.now();
    }

    public boolean isActiveUser() {
        return this.active;
    }

    public String formatCpfCnpj() {
        return ValidationUtil.formatCpfCnpj(this.cpfCnpj);
    }
}