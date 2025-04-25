package br.com.voltz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import br.com.voltz.factory.ConnectionFactory;
import br.com.voltz.model.Wallet;

public class WalletDao {

    public WalletDao() {
    }

    public int save(Wallet wallet) throws SQLException {
        if (wallet == null || wallet.getUserId() <= 0) {
            throw new IllegalArgumentException("Wallet inválida ou sem userId para salvar.");
        }

        String sqlSeq = "SELECT wallets_seq.NEXTVAL FROM dual";
        String sqlInsert = "INSERT INTO wallets (id, user_id, created_at) VALUES (?, ?, ?)";
        int nextId = 0;

        try (Connection connection = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmtSeq = connection.prepareStatement(sqlSeq);
                    ResultSet rsSeq = stmtSeq.executeQuery()) {
                if (rsSeq.next()) {
                    nextId = rsSeq.getInt(1);
                } else {
                    throw new SQLException("Não foi possível obter o próximo valor da sequence wallets_seq.");
                }
            }

            wallet.setId(nextId);

            try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
                stmt.setInt(1, nextId);
                stmt.setInt(2, wallet.getUserId());
                LocalDateTime now = LocalDateTime.now();
                wallet.setCreatedAt(now);
                stmt.setTimestamp(3, Timestamp.valueOf(now));

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao criar carteira, nenhuma linha afetada (INSERT).");
                }
            }
        }
        return nextId;
    }

    public Optional<Wallet> findByUserId(int userId) throws SQLException {
        String sql = "SELECT id, user_id, created_at FROM wallets WHERE user_id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWallet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Wallet> findById(int walletId) throws SQLException {
        String sql = "SELECT id, user_id, created_at FROM wallets WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWallet(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setId(rs.getInt("id"));
        wallet.setUserId(rs.getInt("user_id"));
        Timestamp ts = rs.getTimestamp("created_at");

        if (ts != null) {
            wallet.setCreatedAt(ts.toLocalDateTime());
        }

        return wallet;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM wallets WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}