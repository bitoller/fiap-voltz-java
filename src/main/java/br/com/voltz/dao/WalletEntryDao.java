package br.com.voltz.dao;

import br.com.voltz.model.WalletEntry;
import br.com.voltz.factory.ConnectionFactory;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WalletEntryDao {

    public WalletEntryDao() {
    }

    public void save(WalletEntry entry) throws SQLException {
        String sql = "INSERT INTO wallet_entries (wallet_id, crypto_symbol, amount, last_updated) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            entry.setLastUpdated(now);

            stmt.setInt(1, entry.getWalletId());
            stmt.setString(2, entry.getCryptoSymbol());
            stmt.setBigDecimal(3, entry.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(now));

            stmt.executeUpdate();
        }
    }

    public void updateAmount(int walletId, String cryptoSymbol, BigDecimal newAmount) throws SQLException {
        String sql = "UPDATE wallet_entries SET amount = ?, last_updated = ? WHERE wallet_id = ? AND crypto_symbol = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setBigDecimal(1, newAmount);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setInt(3, walletId);
            stmt.setString(4, cryptoSymbol);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.printf("Aviso: Nenhuma entrada de carteira atualizada para walletId=%d e symbol=%s\n", walletId, cryptoSymbol);
            }
        }
    }

    public void update(WalletEntry entry) throws SQLException {
        String sql = "UPDATE wallet_entries SET wallet_id = ?, crypto_symbol = ?, amount = ?, last_updated = ? WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            entry.setLastUpdated(now);

            stmt.setInt(1, entry.getWalletId());
            stmt.setString(2, entry.getCryptoSymbol());
            stmt.setBigDecimal(3, entry.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setInt(5, entry.getId());

            stmt.executeUpdate();
        }
    }


    public List<WalletEntry> findByWalletId(int walletId) throws SQLException {
        List<WalletEntry> entries = new ArrayList<>();
        String sql = "SELECT id, wallet_id, crypto_symbol, amount, last_updated FROM wallet_entries WHERE wallet_id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToWalletEntry(rs));
                }
            }
        }
        return entries;
    }

    public Optional<WalletEntry> findByWalletIdAndSymbol(int walletId, String cryptoSymbol) throws SQLException {
        String sql = "SELECT id, wallet_id, crypto_symbol, amount, last_updated FROM wallet_entries WHERE wallet_id = ? AND crypto_symbol = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);
            stmt.setString(2, cryptoSymbol);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWalletEntry(rs));
                }
            }
        }
        return Optional.empty();
    }

    private WalletEntry mapResultSetToWalletEntry(ResultSet rs) throws SQLException {
        WalletEntry entry = new WalletEntry();
        entry.setId(rs.getInt("id"));
        entry.setWalletId(rs.getInt("wallet_id"));
        entry.setCryptoSymbol(rs.getString("crypto_symbol"));
        entry.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("last_updated");

        if (ts != null) {
            entry.setLastUpdated(ts.toLocalDateTime());
        }
        return entry;
    }
}