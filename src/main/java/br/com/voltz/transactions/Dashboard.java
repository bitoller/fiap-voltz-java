//package br.com.voltz.transactions;
//
//import java.util.List;
//
//import br.com.voltz.crypto.CryptoAsset;
//import br.com.voltz.crypto.Wallet;
//
//public class Dashboard {
//    private List<Transaction> reports;
//    private List<Double> variationChart;
//    private Wallet wallet;
//
//    public Dashboard(Wallet wallet) {
//        this.wallet = wallet;
//    }
//
//    public void generateChart() {
//        // Espaço reservado para a lógica de geração de gráficos
//        // Esta parte precisa ser implementada usando uma biblioteca de gráficos (por exemplo, JFreeChart)
//    }
//
//    public void viewSummary() {
//        displayWalletSummary();
//        displayCryptoAssetsSummary();
//    }
//
//    private void displayWalletSummary() {
//        System.out.println("Resumo da Carteira:");
//        System.out.println("Saldo total: " + wallet.getTotalBalance());
//    }
//
//    private void displayCryptoAssetsSummary() {
//        for (CryptoAsset cryptoAsset : wallet.getCryptoAssets()) {
//            System.out.println(
//                    "Criptoativo: " + cryptoAsset.getName() + ", Valor total: " + cryptoAsset.calculateTotalValue());
//        }
//    }
//
//    public List<Transaction> getReports() {
//        return reports;
//    }
//
//    public List<Double> getVariationChart() {
//        return variationChart;
//    }
//
//    public Wallet getWallet() {
//        return wallet;
//    }
//
//    public void setReports(List<Transaction> reports) {
//        this.reports = reports;
//    }
//
//    public void setVariationChart(List<Double> variationChart) {
//        this.variationChart = variationChart;
//    }
//
//    public void setWallet(Wallet wallet) {
//        this.wallet = wallet;
//    }
//}
