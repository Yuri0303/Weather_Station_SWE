package org.example.orm;

import org.example.domainmodel.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class UserDAO implements AutoCloseable {
    private Connection connection;

    public UserDAO() {
        try {
            this.connection = DatabaseManager.getConnection();
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


    public boolean registerUser(String firstName, String lastName, String email, String password){
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

    public User login(String email, String password){
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"),
                            resultSet.getString("email"), resultSet.getBoolean("isBlocked"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.getStackTrace();
        }
        return user;
    }

    public boolean blockUser(int id) {
        boolean blocked = false;
        try (PreparedStatement statement = connection.prepareStatement("UPDATE USER SET isBlocked = true WHERE id = ?")) {
            statement.setInt(1, id);

            int updateCount = statement.executeUpdate();
            if (updateCount == 1)
                blocked = true;
        } catch (SQLException e) {
            System.err.println("BlockUser error: " + e.getMessage());
            e.getStackTrace();
        }
        return blocked;
    }

    @Override
    public void close() throws SQLException{
        if(connection != null)
            this.connection.close();
    }
}