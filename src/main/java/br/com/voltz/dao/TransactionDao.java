package br.com.voltz.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.voltz.model.Transaction;

public class TransactionDao extends BaseDao {
    private static final String SEQUENCE_NAME = "transactions_seq";
    private static final String TABLE_NAME = "transactions";

    public int save(Transaction transaction) throws SQLException {
        int nextId = getNextSequenceValue(SEQUENCE_NAME);
        transaction.setId(nextId);

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        BigDecimal amount = transaction.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SQLException("The value must be greater than zero");
        }

        amount = amount.setScale(DEFAULT_DECIMAL_SCALE, java.math.RoundingMode.HALF_UP);

        String sql = "INSERT INTO " + TABLE_NAME +
                " (id, type, source_wallet_id, destination_wallet_id, crypto_symbol, amount, transaction_date, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        executeUpdate(sql,
                nextId,
                transaction.getType(),
                transaction.getSourceWalletId(),
                transaction.getDestinationWalletId(),
                transaction.getCryptoSymbol(),
                amount,
                Timestamp.valueOf(transaction.getTransactionDate()),
                transaction.getStatus());

        return nextId;
    }

    public Optional<Transaction> findById(int transactionId) throws SQLException {
        validateId(transactionId, "transaction");
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        Transaction result = executeQuery(sql, rs -> {
            try {
                return rs.next() ? mapResultSetToTransaction(rs) : null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, transactionId);

        return Optional.ofNullable(result);
    }

    public List<Transaction> findByWalletId(int walletId) throws SQLException {
        validateId(walletId, "wallet");
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE source_wallet_id = ? OR destination_wallet_id = ? " +
                "ORDER BY transaction_date DESC";

        return executeQuery(sql, rs -> {
            try {
                return mapResultSetToTransactionList(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, walletId, walletId);
    }

    public List<Transaction> findRecentByWalletId(int walletId, int limit) throws SQLException {
        validateId(walletId, "wallet");

        if (limit <= 0) {
            throw new IllegalArgumentException("O limite deve ser maior que zero");
        }

        String sql = "SELECT * FROM (" +
                "SELECT * FROM " + TABLE_NAME +
                " WHERE source_wallet_id = ? OR destination_wallet_id = ? " +
                "ORDER BY transaction_date DESC" +
                ") WHERE ROWNUM <= ?";

        return executeQuery(sql, rs -> {
            try {
                return mapResultSetToTransactionList(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, walletId, walletId, limit);
    }

    private List<Transaction> mapResultSetToTransactionList(ResultSet rs) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        while (rs.next()) {
            transactions.add(mapResultSetToTransaction(rs));
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