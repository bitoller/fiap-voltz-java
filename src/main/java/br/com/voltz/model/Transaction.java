package br.com.voltz.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String type;
    private Integer sourceWalletId;
    private Integer destinationWalletId;
    private String cryptoSymbol;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(Integer sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    public Integer getDestinationWalletId() {
        return destinationWalletId;
    }

    public void setDestinationWalletId(Integer destinationWalletId) {
        this.destinationWalletId = destinationWalletId;
    }

    public String getCryptoSymbol() {
        return cryptoSymbol;
    }

    public void setCryptoSymbol(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}