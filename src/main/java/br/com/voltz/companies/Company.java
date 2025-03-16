package br.com.voltz.companies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.voltz.entities.Entity;

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

    public boolean saveToDatabase(Connection connection) throws SQLException {
        String sql = "INSERT INTO companies (id, name, available_balance, bank_account) VALUES (company_seq.NEXTVAL, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, this.getName());
            statement.setDouble(2, this.availableBalance);
            statement.setString(3, this.bankAccount);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }
    }
}