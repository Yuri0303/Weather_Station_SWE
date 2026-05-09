package org.example.orm;

import org.example.domainmodel.Maintainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaintainerDAO {
    private Connection connection;

    public MaintainerDAO(){
        try {
            this.connection = DatabaseManager.getInstance().getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    public Maintainer loginMaintainer(String email, String password) throws SQLException {
        Maintainer maintainer = null;

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM MAINTAINER WHERE email = ? AND password = ?")){
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    maintainer = new Maintainer(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("email"));
                }
            }
        }catch (SQLException e){
            System.err.println("Errore durante il login di maintainer");
            e.printStackTrace();
        }

        return maintainer;
    }
}
