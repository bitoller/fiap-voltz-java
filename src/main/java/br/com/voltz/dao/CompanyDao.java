package br.com.voltz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.voltz.companies.Company;
import br.com.voltz.factory.ConnectionFactory;

public class CompanyDao {

    public boolean insert(Company company) throws SQLException {
        String sql = "INSERT INTO companies (id, name, available_balance, bank_account) VALUES (company_seq.NEXTVAL, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, company.getName());
            statement.setDouble(2, company.getAvailableBalance());
            statement.setString(3, company.getBankAccount());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(Company company) throws SQLException {
        String sql = "UPDATE companies SET name = ?, available_balance = ?, bank_account = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, company.getName());
            statement.setDouble(2, company.getAvailableBalance());
            statement.setString(3, company.getBankAccount());
            statement.setInt(4, company.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int companyId) throws SQLException {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, companyId);
            return statement.executeUpdate() > 0;
        }
    }
}
