package main.java.voltz.companies;

public class Company {
    private String name;
    private double availableBalance;
    private String bankAccount;

    public Company(String name, double availableBalance, String bankAccount) {
        this.name = name;
        this.availableBalance = availableBalance;
        this.bankAccount = bankAccount;
    }

    public boolean sendAmount(double amount) {
        if (amount > 0 && amount <= availableBalance) {
            availableBalance -= amount;
            return true;
        }
        return false;
    }

    public double checkBalance() {
        return availableBalance;
    }

    public String getName() {
        return name;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void decreaseBalance(double amount) {
        if (amount > 0 && availableBalance >= amount) {
            availableBalance -= amount;
        }
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
}