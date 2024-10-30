public class CryptoAsset {
    private String name;
    public double quantity;
    private double currentValue;

    public CryptoAsset(String name, double quantity, double currentValue) {
        this.name = name;
        this.quantity = quantity;
        this.currentValue = currentValue;
    }

    public void updateValue(double newValue) {
        this.currentValue = newValue;
    }

    public double calculateTotalValue() {
        return this.quantity * this.currentValue;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getCurrentValue() {
        return currentValue;
    }
}