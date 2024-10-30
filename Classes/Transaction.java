import java.util.Date;

public class Transaction {
    private String type;
    private double quantity;
    private Date date;
    private double value;
    private CryptoAsset cryptoAsset;

    public Transaction(String type, double quantity, Date date, double value, CryptoAsset cryptoAsset) {
        this.type = type;
        this.quantity = quantity;
        this.date = date;
        this.value = value;
        this.cryptoAsset = cryptoAsset;
    }

    public synchronized void executeTransaction(Wallet wallet) {
        if (validateTransaction(wallet)) {
            if (type.equals("buy")) {
                wallet.updateTotalBalance(-value);
                cryptoAsset.quantity += quantity;
            } else if (type.equals("sell")) {
                wallet.updateTotalBalance(value);
                cryptoAsset.quantity -= quantity;
            }

            recordTransaction(wallet);
        }
    }

    public boolean validateTransaction(Wallet wallet) {
        if (type.equals("buy")) {
            return wallet.getTotalBalance() >= value && quantity > 0;
        } else if (type.equals("sell")) {
            return cryptoAsset.getQuantity() >= quantity && quantity > 0;
        }

        return false;
    }

    public void recordTransaction(Wallet wallet) {
        wallet.getTransactions().add(this);
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public Date getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public CryptoAsset getCryptoAsset() {
        return cryptoAsset;
    }
}