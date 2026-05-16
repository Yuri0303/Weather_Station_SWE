package org.example.businesslogic;

import org.example.domainmodel.Admin;
import org.example.domainmodel.Maintainer;
import org.example.orm.AdminDAO;
import org.example.orm.MaintainerDAO;

import java.sql.SQLException;

public class StaffLoginController {
    public StaffLoginController() {}
    public Admin adminLogin(String email, String password) {
        try (AdminDAO adminDAO = new AdminDAO()) {
            return adminDAO.login(email, password);
        } catch (SQLException e) {
            System.err.println("Errore durante il login admin: " + e.getMessage());
            return null;
        }
    }

    public Maintainer loginMaintainer(String email, String password) {
        try(MaintainerDAO maintainerDAO = new MaintainerDAO()){
            return maintainerDAO.loginMaintainer(email, password);
        }catch (SQLException e) {
            System.err.println("Errore durante il login maintainer: " + e.getMessage());
            return null;
        }

    }
}
