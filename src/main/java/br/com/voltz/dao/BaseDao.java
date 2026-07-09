package br.com.voltz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.function.Function;

import br.com.voltz.factory.ConnectionFactory;

public abstract class BaseDao {
    protected static final int DEFAULT_DECIMAL_SCALE = 18;

    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    protected int getNextSequenceValue(String sequenceName) throws SQLException {
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM dual";
        try (Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException("Could not obtain the next sequence value for " + sequenceName);
            }
            return rs.getInt(1);
        }
    }

    protected Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    protected <T> T executeQuery(String sql, Function<ResultSet, T> mapper, Object... params) throws SQLException {
        try (Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                setParameter(stmt, i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.apply(rs);
            }
        }
    }

    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                setParameter(stmt, i + 1, params[i]);
            }

            return stmt.executeUpdate();
        }
    }

    private void setParameter(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.NULL);
        } else if (value instanceof String) {
            stmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            stmt.setInt(index, (Integer) value);
        } else if (value instanceof Long) {
            stmt.setLong(index, (Long) value);
        } else if (value instanceof Double) {
            stmt.setDouble(index, (Double) value);
        } else if (value instanceof java.math.BigDecimal) {
            stmt.setBigDecimal(index, (java.math.BigDecimal) value);
        } else if (value instanceof LocalDateTime) {
            stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        } else if (value instanceof Timestamp) {
            stmt.setTimestamp(index, (Timestamp) value);
        } else if (value instanceof Boolean) {
            stmt.setString(index, ((Boolean) value) ? "S" : "N");
        } else {
            throw new SQLException("Unsupported parameter type: " + value.getClass());
        }
    }

    protected void validateId(int id, String entityName) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid " + entityName + " ID: " + id);
        }
    }
}