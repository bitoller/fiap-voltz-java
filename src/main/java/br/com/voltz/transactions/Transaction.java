//package br.com.voltz.transactions;
//
//import br.com.voltz.crypto.CryptoAsset;
//import br.com.voltz.crypto.Wallet;
//
//import java.util.Date;
//
//public class Transaction {
//    private String type;
//    private double quantity;
//    private Date date;
//    private double value;
//    private CryptoAsset cryptoAsset;
//
//    public Transaction(String type, double quantity, Date date, double value, CryptoAsset cryptoAsset) {
//        this.type = type;
//        this.quantity = quantity;
//        this.date = date;
//        this.value = value;
//        this.cryptoAsset = cryptoAsset;
//    }
//
//    public Transaction(String type, double quantity, double value, CryptoAsset cryptoAsset) {
//        this(type, quantity, new Date(), value, cryptoAsset);
//    }
//
//    public synchronized void executeTransaction(Wallet wallet) {
//        if (validateTransaction(wallet)) {
//            processTransaction(wallet);
//            recordTransaction(wallet);
//        }
//    }
//
//    private void processTransaction(Wallet wallet) {
//        if (type.equals("buy")) {
//            wallet.updateTotalBalance(-value);
//            cryptoAsset.setQuantity(cryptoAsset.getQuantity() + quantity);
//        } else if (type.equals("sell")) {
//            wallet.updateTotalBalance(value);
//            cryptoAsset.setQuantity(cryptoAsset.getQuantity() - quantity);
//        }
//    }
//
//    public boolean validateTransaction(Wallet wallet) {
//        if (type.equals("buy")) {
//            return wallet.getTotalBalance() >= value && quantity > 0;
//        } else if (type.equals("sell")) {
//            return cryptoAsset.getQuantity() >= quantity && quantity > 0;
//        }
//        return false;
//    }
//
//    private void recordTransaction(Wallet wallet) {
//        wallet.addTransaction(this);
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public double getQuantity() {
//        return quantity;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public double getValue() {
//        return value;
//    }
//
//    public CryptoAsset getCryptoAsset() {
//        return cryptoAsset;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public void setQuantity(double quantity) {
//        this.quantity = quantity;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public void setValue(double value) {
//        this.value = value;
//    }
//
//    public void setCryptoAsset(CryptoAsset cryptoAsset) {
//        this.cryptoAsset = cryptoAsset;
//    }
//}
