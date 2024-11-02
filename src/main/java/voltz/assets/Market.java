package main.java.voltz.assets;

public class Market {
    private double exchangeRates;

    public Market(double exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public void updateQuotations() {
        // Placeholder for logic to update crypto asset quotations
        // Example: use external APIs to get data
        // and update the values of crypto assets in the CryptoAsset class
    }

    public void checkCryptoAssetValue(String cryptoAssetName) {
        // Placeholder for logic to check the value of a specific crypto asset
    }

    public double getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(double exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}