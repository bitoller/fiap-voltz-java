package main.java.voltz.companies;
import main.java.voltz.users.User;

public class Company extends User {
    private String name;
    private double availableBalance;
    private String bankAccount;

    public Company(String name, double availableBalance, String bankAccount) {
        super(name, "defaultEmail@example.com", "defaultPassword", true);
        this.name = name;
        this.availableBalance = availableBalance;
        this.bankAccount = bankAccount;
    }

    public boolean sendAmount(double amount) {
        if (amount > 0 && amount <= availableBalance) {
            availableBalance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public double checkBalance() {
        return availableBalance;
    }
}
