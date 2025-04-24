package br.com.voltz.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletEntry {
    private int id;
    private int walletId;
    private String cryptoSymbol;
    private BigDecimal amount;
    private LocalDateTime lastUpdated;

    public WalletEntry() {}

    public WalletEntry(int walletId, String cryptoSymbol, BigDecimal amount) {
        this.walletId = walletId;
        this.cryptoSymbol = cryptoSymbol;
        this.amount = amount;
        this.lastUpdated = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }
    public String getCryptoSymbol() { return cryptoSymbol; }
    public void setCryptoSymbol(String cryptoSymbol) { this.cryptoSymbol = cryptoSymbol; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}