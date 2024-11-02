package main.java.voltz.crypto;

import java.util.ArrayList;
import java.util.List;
import main.java.voltz.transactions.Transaction;

public class Wallet {
    private List<CryptoAsset> cryptoAssets;
    private List<Transaction> transactions;
    private double totalBalance;

    public Wallet() {
        this.cryptoAssets = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.totalBalance = 0.0;
    }

    public void addCryptoAsset(CryptoAsset cryptoAsset) {
        this.cryptoAssets.add(cryptoAsset);
        updateTotalBalance(cryptoAsset.calculateTotalValue());
    }

    public void removeCryptoAsset(CryptoAsset cryptoAsset) {
        this.cryptoAssets.remove(cryptoAsset);
        updateTotalBalance(-cryptoAsset.calculateTotalValue());
    }

    public void updateTotalBalance(double value) {
        this.totalBalance += value;
    }

    public List<CryptoAsset> getCryptoAssets() {
        return cryptoAssets;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void setCryptoAssets(List<CryptoAsset> cryptoAssets) {
        this.cryptoAssets = cryptoAssets;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }
}