package org.example.businesslogic;

import org.example.domainmodel.User;
import org.example.orm.UserDAO;

import java.sql.SQLException;

public class UserLoginController {

    public User login(String email, String password){
        try (UserDAO userDAO = new UserDAO()) {
            return userDAO.login(email, password);
        }catch (SQLException e){
            System.err.println("Errore durante il login dello User" + e.getMessage());
            return null;
        }
    }

    public boolean register(String firstName, String lastName, String email, String password){

        boolean success = false;

        try (UserDAO userDAO = new UserDAO()) {
            if(userDAO.login(email, password) == null)
                success = userDAO.registerUser(firstName, lastName, email , password);
        }catch (SQLException e){
            System.err.println("Errore durante la registrazione dello User" + e.getMessage());
        }
        return success;
    }

}
