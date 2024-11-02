package main.java.voltz.crypto;

public class Market {
    private double exchangeRates;

    public Market(double exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public void updateQuotations() {
        // Espaço reservado para a lógica de atualização das cotações de criptoativos
        // Usar APIs externas para obter dados e atualizar os valores dos criptoativos na classe CryptoAsset
    }

    public void checkCryptoAssetValue(String cryptoAssetName) {
        // Espaço reservado para a lógica de verificação do valor de um criptoativo específico
    }

    public double getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(double exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}