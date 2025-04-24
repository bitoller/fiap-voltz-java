package br.com.voltz.dao;

import br.com.voltz.model.Users;
import br.com.voltz.factory.ConnectionFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersDao {

    private String hashPassword(String plainTextPassword){
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia para hashing.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty() || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao verificar senha com BCrypt (hash malformado?): " + e.getMessage());
            return false;
        }
    }

    public int save(Users user) throws SQLException {
        if (user == null || user.getEmail() == null || user.getSenha() == null) {
            throw new IllegalArgumentException("Objeto usuário, email ou senha não podem ser nulos para salvar.");
        }

        String sqlSeq = "SELECT users_seq.NEXTVAL FROM dual";
        String sqlInsert = "INSERT INTO users (id, nome, cpf_cnpj, email, telefone, senha, ativo, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int nextId = 0;

        try (Connection connection = ConnectionFactory.getConnection()) {
            // 1. Obter o próximo ID da sequence
            try (PreparedStatement stmtSeq = connection.prepareStatement(sqlSeq);
                 ResultSet rsSeq = stmtSeq.executeQuery()) {
                if (rsSeq.next()) {
                    nextId = rsSeq.getInt(1);
                } else {
                    throw new SQLException("Não foi possível obter o próximo valor da sequence users_seq.");
                }
            }

            user.setId(nextId);

            try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
                stmt.setInt(1, nextId); // Usa o ID obtido da sequence
                stmt.setString(2, user.getNome());
                stmt.setString(3, user.getCpfCnpj());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getTelefone());
                stmt.setString(6, hashPassword(user.getSenha()));
                stmt.setString(7, user.isAtivo() ? "S" : "N");
                LocalDateTime now = LocalDateTime.now();
                user.setDataCriacao(now);
                user.setDataAtualizacao(now);
                stmt.setTimestamp(8, Timestamp.valueOf(now));
                stmt.setTimestamp(9, Timestamp.valueOf(now));

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao criar usuário, nenhuma linha afetada (INSERT).");
                }
            }
        }
        return nextId;
    }


    public void update(Users user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Usuário inválido ou sem ID para atualização.");
        }
        LocalDateTime now = LocalDateTime.now();
        user.setDataAtualizacao(now);

        boolean updatePassword = user.getSenha() != null && !user.getSenha().isEmpty() && !user.getSenha().startsWith("$2a$");

        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET nome = ?, cpf_cnpj = ?, email = ?, telefone = ?, senha = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET nome = ?, cpf_cnpj = ?, email = ?, telefone = ?, ativo = ?, data_atualizacao = ? WHERE id = ?";
        }

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getNome());
            stmt.setString(2, user.getCpfCnpj());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getTelefone());

            int parameterIndex;
            if (updatePassword) {
                stmt.setString(5, hashPassword(user.getSenha()));
                stmt.setString(6, user.isAtivo() ? "S" : "N");
                stmt.setTimestamp(7, Timestamp.valueOf(now));
                parameterIndex = 8;
            } else {
                stmt.setString(5, user.isAtivo() ? "S" : "N");
                stmt.setTimestamp(6, Timestamp.valueOf(now));
                parameterIndex = 7;
            }
            stmt.setInt(parameterIndex, user.getId());

            stmt.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido para exclusão.");
        }
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    public Optional<Users> findById(int id) throws SQLException {
        if (id <= 0) return Optional.empty();

        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, senha, ativo, data_criacao, data_atualizacao FROM users WHERE id = ?";
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
        if (email == null || email.trim().isEmpty()) return Optional.empty();

        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, senha, ativo, data_criacao, data_atualizacao FROM users WHERE email = ?";
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
        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, senha, ativo, data_criacao, data_atualizacao FROM users";
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
        user.setNome(rs.getString("nome"));
        user.setCpfCnpj(rs.getString("cpf_cnpj"));
        user.setEmail(rs.getString("email"));
        user.setTelefone(rs.getString("telefone"));
        user.setSenha(rs.getString("senha")); // Guarda o HASH
        user.setAtivo("S".equalsIgnoreCase(rs.getString("ativo")));

        Timestamp tsCriacao = rs.getTimestamp("data_criacao");
        if (tsCriacao != null) {
            user.setDataCriacao(tsCriacao.toLocalDateTime());
        }
        Timestamp tsAtualizacao = rs.getTimestamp("data_atualizacao");
        if (tsAtualizacao != null) {
            user.setDataAtualizacao(tsAtualizacao.toLocalDateTime());
        }
        return user;
    }
}