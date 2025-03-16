package br.com.voltz.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionFactory {
    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final String USER = "rm553134";
    private static final String PASSWORD = "060892";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), (int) TimeUnit.MINUTES.toMillis(2));
        return connection;
    }
}