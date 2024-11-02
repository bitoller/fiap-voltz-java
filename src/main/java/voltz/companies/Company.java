package main.java.voltz.companies;

import main.java.voltz.entities.Entity;

public class Company extends Entity {
    private double availableBalance;
    private String bankAccount;

    public Company(String name, double availableBalance, String bankAccount) {
        super(name);
        this.availableBalance = availableBalance;
        this.bankAccount = bankAccount;
    }

    @Override
    public void displayInfo() {
        System.out.println("Nome da Empresa: " + getName());
        System.out.println("Saldo Disponível: " + availableBalance);
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

    public double getAvailableBalance() {
        return availableBalance;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void decreaseBalance(double amount) {
        if (amount > 0 && availableBalance >= amount) {
            availableBalance -= amount;
        }
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
}