package org.example.orm;

import org.example.domainmodel.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        try {
            this.connection = DatabaseManager.getInstance().getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public ArrayList<User> getUsers(Map<String, Object> param) {
        StringBuilder query = new StringBuilder("SELECT * FROM USER");

        if(param != null && !param.isEmpty()) {
            query.append(" WHERE ");
            for (String key : param.keySet()) {
                query.append(key).append(" = ? AND ");
            }
            query.setLength(query.length() - 5);
        }

        ArrayList<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            if (param != null && !param.isEmpty()) {
                int paramIndex = 1;
                for (Object value : param.values()) {
                    statement.setObject(paramIndex, value);
                    paramIndex++;
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(new User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"),
                            resultSet.getString("email"), resultSet.getBoolean("isBlocked")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during users retrieve: " + e.getMessage());
            e.getStackTrace();
        }

        return users;
    }


    public boolean registerUser(String firstName, String lastName, String email, String password) {//fixme decidere se tenere questa versione o quella tramite eccezione
        boolean registered = false;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO USER (firstName, lastName, email, password, isBlocked) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, password);
            statement.setBoolean(5, false);

            int insertCount = statement.executeUpdate();
            if (insertCount == 1)
                registered = true;

        } catch (SQLException e) {
            System.err.println("Sing-up error: " + e.getMessage());
            e.getStackTrace();
        }
        return registered;
    }

}