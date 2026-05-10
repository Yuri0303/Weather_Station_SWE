package org.example.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String url = "jdbc:postgresql://localhost:5432/weather_station_db";
    private static final String username = "Weather_S_admin";
    private static final String password = "admin";
    private static Connection connection = null;

    // singleton instance
    private static DatabaseManager instance = null;

    private DatabaseManager(){}

    public static DatabaseManager getInstance() {

        if (instance == null) { instance = new DatabaseManager(); }

        return instance;

    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        if (connection == null)
            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }

        return connection;
    }
}
