package br.com.voltz.users;

import br.com.voltz.companies.Company;
import br.com.voltz.crypto.CryptoAsset;
import br.com.voltz.crypto.Wallet;
import br.com.voltz.entities.Entity;

public class User extends Entity {
    private String email;
    private String password;
    private boolean authentication2FA;
    private Wallet wallet;
    private Company company;

    public User(String name, String email, String password, boolean authentication2FA) {
        super(name);
        this.email = email;
        this.password = password;
        this.authentication2FA = authentication2FA;
        this.wallet = new Wallet();
        this.company = null;
    }

    @Override
    public void displayInfo() {
        System.out.println("Nome do Usuário: " + getName());
        System.out.println("Email: " + email);
    }

    public void register() {
        System.out.println("Usuário " + getName() + " registrado com sucesso.\nEmail: " + email);
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthentication2FA(boolean authentication2FA) {
        this.authentication2FA = authentication2FA;
    }

    public Wallet checkWallet() {
        return this.wallet;
    }

    public void addCryptoToWallet(String assetName, double amount, double currentPrice) {
        CryptoAsset cryptoAsset = new CryptoAsset(assetName, amount, currentPrice);
        wallet.addCryptoAsset(cryptoAsset);
    }

    public boolean isAuthentication2FA() {
        return authentication2FA;
    }

    public boolean sendAmountFromCompany(double amount) {
        if (company != null && company.getAvailableBalance() >= amount) {
            company.decreaseBalance(amount);
            return true;
        }
        return false;
    }

    public Company getCompany() {
        return company;
    }

    public String getCompanyName() {
        return company != null ? company.getName() : "Nenhuma empresa associada";
    }

    public double getCompanyBalance() {
        return company != null ? company.getAvailableBalance() : 0.0;
    }

    public String getEmail() {
        return email;
    }

    public double getWalletTotalBalance() {
        return wallet.getTotalBalance();
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    /* public boolean saveToDatabase(Connection connection) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, password, authentication2FA) VALUES (user_seq.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, this.getName());
            statement.setString(2, this.email);
            statement.setString(3, this.password);
            statement.setBoolean(4, this.authentication2FA);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }
    } */

    /* public boolean saveCompanyToDatabase(Connection connection) throws SQLException {
        if (this.company == null) {
            return false;
        }

        String sql = "INSERT INTO user_companies (user_id, company_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, this.getId());
            statement.setInt(2, this.company.getId());
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }
    } */

    /* public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            Connection connection = ConnectionFactory.getConnection();
            String query = "SELECT id, name, email FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                User user = new User(name, email, null, false);
                user.setId(id);
                userList.add(user);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários no banco de dados: " + e.getMessage());
        }
        return userList;
    } */

    /* public boolean updateUserInDatabase(Connection connection) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, authentication2FA = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, this.getName());
            statement.setString(2, this.email);
            statement.setString(3, this.password);
            statement.setBoolean(4, this.authentication2FA);
            statement.setInt(5, this.getId());
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }
    } */

    /* public static User getUserById(int userId) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            String query = "SELECT id, name, email, password, authentication2FA FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                boolean authentication2FA = resultSet.getBoolean("authentication2FA");
                User user = new User(name, email, password, authentication2FA);
                user.setId(userId);
                return user;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário no banco de dados: " + e.getMessage());
        }
        return null;
    } */

    /* public static boolean deleteUserById(Connection connection, int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e;
        }
    } */

}