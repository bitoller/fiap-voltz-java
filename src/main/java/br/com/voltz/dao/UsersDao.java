package br.com.voltz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import br.com.voltz.factory.ConnectionFactory;
import br.com.voltz.model.Users;

public class UsersDao {

    private String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty for hashing.");
        }

        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()
                || !hashedPassword.startsWith("$2a$")) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Error verifying password with BCrypt (malformed hash?): " + e.getMessage());
            return false;
        }
    }

    public int save(Users user) throws SQLException {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User object, email, or password cannot be null to save.");
        }

        String sqlSeq = "SELECT users_seq.NEXTVAL FROM dual";
        String sqlInsert = "INSERT INTO users (id, user_name, cpf_cnpj, email, phone_number, password, active, date_created, date_updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int nextId = 0;

        try (Connection connection = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmtSeq = connection.prepareStatement(sqlSeq);
                    ResultSet rsSeq = stmtSeq.executeQuery()) {

                if (rsSeq.next()) {
                    nextId = rsSeq.getInt(1);
                } else {
                    throw new SQLException("Could not obtain the next sequence value for users_seq.");
                }
            }

            user.setId(nextId);

            try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
                stmt.setInt(1, nextId);
                stmt.setString(2, user.getUserName());
                stmt.setString(3, user.getCpfCnpj());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getPhoneNumber());
                stmt.setString(6, hashPassword(user.getPassword()));
                stmt.setString(7, user.isActive() ? "S" : "N");
                LocalDateTime now = LocalDateTime.now();
                user.setDateCreated(now);
                user.setDateUpdated(now);
                stmt.setTimestamp(8, Timestamp.valueOf(now));
                stmt.setTimestamp(9, Timestamp.valueOf(now));

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Failed to create user, no rows affected (INSERT).");
                }
            }
        }
        return nextId;
    }

    public void update(Users user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user or missing ID for update.");
        }

        LocalDateTime now = LocalDateTime.now();
        user.setDateUpdated(now);

        boolean updatePassword = user.getPassword() != null && !user.getPassword().isEmpty()
                && !user.getPassword().startsWith("$2a$");

        String sql;

        if (updatePassword) {
            sql = "UPDATE users SET user_name = ?, cpf_cnpj = ?, email = ?, phone_number = ?, password = ?, active = ?, date_updated = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET user_name = ?, cpf_cnpj = ?, email = ?, phone_number = ?, active = ?, date_updated = ? WHERE id = ?";
        }

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getCpfCnpj());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());

            int parameterIndex;

            if (updatePassword) {
                stmt.setString(5, hashPassword(user.getPassword()));
                stmt.setString(6, user.isActive() ? "S" : "N");
                stmt.setTimestamp(7, Timestamp.valueOf(now));
                parameterIndex = 8;
            } else {
                stmt.setString(5, user.isActive() ? "S" : "N");
                stmt.setTimestamp(6, Timestamp.valueOf(now));
                parameterIndex = 7;
            }
            stmt.setInt(parameterIndex, user.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID for deletion.");
        }

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Users> findById(int id) throws SQLException {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT id, user_name, cpf_cnpj, email, phone_number, password, active, date_created, date_updated FROM users WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsers(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Users> findByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty())
            return Optional.empty();

        String sql = "SELECT id, user_name, cpf_cnpj, email, phone_number, password, active, date_created, date_updated FROM users WHERE email = ?";

        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsers(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Users> findAll() throws SQLException {
        List<Users> users = new ArrayList<>();
        String sql = "SELECT id, user_name, cpf_cnpj, email, phone_number, password, active, date_created, date_updated FROM users";

        try (Connection connection = ConnectionFactory.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUsers(rs));
            }
        }
        return users;
    }

    private Users mapResultSetToUsers(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("id"));
        user.setUserName(rs.getString("user_name"));
        user.setCpfCnpj(rs.getString("cpf_cnpj"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPassword(rs.getString("password"));
        user.setActive("S".equalsIgnoreCase(rs.getString("active")));

        Timestamp tsCreated = rs.getTimestamp("date_created");

        if (tsCreated != null) {
            user.setDateCreated(tsCreated.toLocalDateTime());
        }

        Timestamp tsUpdated = rs.getTimestamp("date_updated");

        if (tsUpdated != null) {
            user.setDateUpdated(tsUpdated.toLocalDateTime());
        }

        return user;
    }
}