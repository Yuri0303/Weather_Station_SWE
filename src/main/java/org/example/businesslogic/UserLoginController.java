package org.example.businesslogic;

import org.example.domainmodel.User;
import org.example.orm.UserDAO;

import java.sql.SQLException;

public class UserLoginController {

    public User login(String email, String password){
        UserDAO userDAO = new UserDAO();
        return userDAO.login(email, password);

    }

    public boolean register(String firstName, String lastName, String email, String password){
        UserDAO userDAO = new UserDAO();
        boolean success = false;

        if(userDAO.login(email, password) == null)
            success = userDAO.registerUser(firstName, lastName, email , password);

        return success;
    }

}
