import java.util.List;

public class Dashboard {
    private List<Transaction> reports;
    private List<Double> variationChart;
    private Wallet wallet;

    public Dashboard(Wallet wallet) {
        this.wallet = wallet;
    }

    public void generateChart() {
        // Lógica para gerar o gráfico com base nos dados de transações e criptoativos
        // Essa parte precisa ser implementada usando uma biblioteca de gráficos (ex: Lucas)
        // e ajustada para a sua interface gráfica.
    }

    public void viewSummary() {
        System.out.println("Wallet Summary:");
        System.out.println("Total balance: " + wallet.getTotalBalance());
        
        for (CryptoAsset cryptoAsset : wallet.getCryptoAssets()) {
            System.out.println("CryptoAsset: " + cryptoAsset.getName() + ", Total value: " + cryptoAsset.calculateTotalValue());
        }
    }
}