package main.java.voltz.assets;
import java.util.ArrayList;
import java.util.List;
import main.java.voltz.transactions.Transaction;
import main.java.voltz.users.User;

public class Wallet extends User {
    private List<CryptoAsset> cryptoAssets;
    private List<Transaction> transactions;
    private double totalBalance;

    public Wallet(String firstName, String lastName, String email, boolean isActive) {
        super(firstName, lastName, email, isActive);
        this.cryptoAssets = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.totalBalance = 0.0;
    }

    public void addCryptoAsset(CryptoAsset cryptoAsset) {
        this.cryptoAssets.add(cryptoAsset);
    }

    public void removeCryptoAsset(CryptoAsset cryptoAsset) {
        this.cryptoAssets.remove(cryptoAsset);
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
}
