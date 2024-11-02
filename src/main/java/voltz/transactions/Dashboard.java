package main.java.voltz.transactions;

import java.util.List;
import main.java.voltz.assets.CryptoAsset;
import main.java.voltz.assets.Wallet;

public class Dashboard {
    private List<Transaction> reports;
    private List<Double> variationChart;
    private Wallet wallet;

    public Dashboard(Wallet wallet) {
        this.wallet = wallet;
    }

    public void generateChart() {
        // Placeholder for chart generation logic
        // This part needs to be implemented using a chart library (e.g., JFreeChart)
        // and adjusted for your graphical interface.
    }

    public void viewSummary() {
        displayWalletSummary();
        displayCryptoAssetsSummary();
    }

    private void displayWalletSummary() {
        System.out.println("Wallet Summary:");
        System.out.println("Total balance: " + wallet.getTotalBalance());
    }

    private void displayCryptoAssetsSummary() {
        for (CryptoAsset cryptoAsset : wallet.getCryptoAssets()) {
            System.out.println(
                    "CryptoAsset: " + cryptoAsset.getName() + ", Total value: " + cryptoAsset.calculateTotalValue());
        }
    }

    public List<Transaction> getReports() {
        return reports;
    }

    public void setReports(List<Transaction> reports) {
        this.reports = reports;
    }

    public List<Double> getVariationChart() {
        return variationChart;
    }

    public void setVariationChart(List<Double> variationChart) {
        this.variationChart = variationChart;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}