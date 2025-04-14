// UsersDao.java (br.com.voltz.usuarios)
package br.com.voltz.usuarios;

import br.com.voltz.users.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsersDao {

    private Connection connection;

    public UsersDao(Connection connection) {
        this.connection = connection;
    }

    // Método para criptografar a senha
    private String encryptPassword(String senha) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(senha.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // INSERT
    public void insert(Users usuario) throws SQLException, NoSuchAlgorithmException {
        String sql = "INSERT INTO USERS (nome, cpf_cnpj, email, telefone, senha, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpfCnpj());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, encryptPassword(usuario.getSenha())); // Criptografando a senha
            stmt.setString(6, usuario.isAtivo() ? "S" : "N");
            stmt.executeUpdate();
        }
    }

    // UPDATE
    public void update(Users usuario) throws SQLException, NoSuchAlgorithmException {
        String sql = "UPDATE USERS SET nome = ?, cpf_cnpj = ?, email = ?, telefone = ?, senha = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpfCnpj());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, encryptPassword(usuario.getSenha())); // Criptografando a senha
            stmt.setString(6, usuario.isAtivo() ? "S" : "N");
            stmt.setInt(7, usuario.getId());
            stmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM USERS WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // FIND BY ID
    public Users findById(int id) throws SQLException {
        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, senha, ativo FROM USERS WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    // FIND ALL
    public List<Users> findAll() throws SQLException {
        List<Users> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, senha, ativo FROM USERS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    // Map ResultSet to Usuario
    private Users mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Users usuario = new Users();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpfCnpj(rs.getString("cpf_cnpj"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setSenha(rs.getString("senha")); // Senha já criptografada no banco
        usuario.setAtivo("S".equalsIgnoreCase(rs.getString("ativo")));
        return usuario;
    }
}