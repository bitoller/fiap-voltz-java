//package br.com.voltz.crypto;
//
//import br.com.voltz.factory.ConnectionFactory;
//import br.com.voltz.users.User;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CryptoAsset {
//    private String name;
//    private double quantity;
//    private double currentValue;
//
//    public CryptoAsset(String name, double quantity, double currentValue) {
//        this.name = name;
//        this.quantity = quantity;
//        this.currentValue = currentValue;
//    }
//
//    public void updateValue(double newValue) {
//        this.currentValue = newValue;
//    }
//
//    public double calculateTotalValue() {
//        return this.quantity * this.currentValue;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public double getQuantity() {
//        return quantity;
//    }
//
//    public double getCurrentValue() {
//        return currentValue;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setQuantity(double quantity) {
//        this.quantity = quantity;
//    }
//
//    public void setCurrentValue(double currentValue) {
//        this.currentValue = currentValue;
//    }
//
//    public static List<CryptoAsset> getAllCrypto() {
//        List<CryptoAsset> CryptoList = new ArrayList<>();
//
//        try {
//            Connection connection = ConnectionFactory.getConnection();
//            String query = "SELECT nome, quantity, corrent FROM crypto_assets";
//            PreparedStatement statement = connection.prepareStatement(query);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                String email = resultSet.getString("email");
//                User user = new User(name, email, null, false);
//                user.setId(id);
//                userList.add(user);
//            }
//
//            resultSet.close();
//            statement.close();
//            connection.close();
//        } catch (SQLException e) {
//            System.out.println("Erro ao buscar usuários no banco de dados: " + e.getMessage());
//        }
//        return userList;
//    }
//}
