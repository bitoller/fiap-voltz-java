package br.com.voltz.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private int id;
    private int userId;
    private LocalDateTime createdAt;
    private List<WalletEntry> entries;

    public Wallet() {
        this.entries = new ArrayList<>();
    }

    public Wallet(int userId) {
        this();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<WalletEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WalletEntry> entries) {
        this.entries = entries;
    }
}