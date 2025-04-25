package br.com.voltz.dao;

import br.com.voltz.model.Transaction;
import br.com.voltz.factory.ConnectionFactory;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDao {

    public TransactionDao() {
    }

    public int save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (type, source_wallet_id, destination_wallet_id, crypto_symbol, amount, transaction_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();

            if (transaction.getTransactionDate() == null) {
                transaction.setTransactionDate(now);
            }

            stmt.setString(1, transaction.getType());

            if (transaction.getSourceWalletId() != null) {
                stmt.setInt(2, transaction.getSourceWalletId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (transaction.getDestinationWalletId() != null) {
                stmt.setInt(3, transaction.getDestinationWalletId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, transaction.getCryptoSymbol());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionDate()));
            stmt.setString(7, transaction.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar transação, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    transaction.setId(id);
                    return id;
                } else {
                    throw new SQLException("Falha ao criar transação, não obteve o ID.");
                }
            }
        }
    }

    public Optional<Transaction> findById(int transactionId) throws SQLException {
        String sql = "SELECT id, type, source_wallet_id, destination_wallet_id, crypto_symbol, amount, transaction_date, status FROM transactions WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Transaction> findByWalletId(int walletId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, type, source_wallet_id, destination_wallet_id, crypto_symbol, amount, transaction_date, status FROM transactions " +
                "WHERE source_wallet_id = ? OR destination_wallet_id = ? " +
                "ORDER BY transaction_date DESC";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, walletId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    public List<Transaction> findRecentByWalletId(int walletId, int limit) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, type, source_wallet_id, destination_wallet_id, crypto_symbol, amount, transaction_date, status FROM transactions " +
                "WHERE source_wallet_id = ? OR destination_wallet_id = ? " +
                "ORDER BY transaction_date DESC LIMIT ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setInt(2, walletId);
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setId(rs.getInt("id"));
        tx.setType(rs.getString("type"));
        tx.setSourceWalletId(rs.getObject("source_wallet_id", Integer.class));
        tx.setDestinationWalletId(rs.getObject("destination_wallet_id", Integer.class));
        tx.setCryptoSymbol(rs.getString("crypto_symbol"));
        tx.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("transaction_date");

        if (ts != null) {
            tx.setTransactionDate(ts.toLocalDateTime());
        }
        tx.setStatus(rs.getString("status"));
        return tx;
    }
}