package org.example.orm;

import org.example.domainmodel.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    private Connection connection;

    public AdminDAO(){
        try {
            this.connection = DatabaseManager.getInstance().getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public Admin login(String email, String password){
        Admin admin = null;
        String query = "SELECT * FROM ADMIN WHERE email = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    admin = new Admin(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("email"));
                }
            }
        }catch (SQLException e){
            System.err.println("Errore durante il login di Admin");
            e.getStackTrace();
        }

        return admin;
    }

    public void resetDatabase(){

    }

    public void createDatabase(){

    }

    public void generateDefaultInstances(){

    }
}
