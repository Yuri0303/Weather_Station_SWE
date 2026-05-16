package org.example.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String url = "jdbc:postgresql://localhost:5432/weather_station_db";
    private static final String username = "Weather_S_admin";
    private static final String password = "admin";


    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        return DriverManager.getConnection(url, username, password);
    }
}
