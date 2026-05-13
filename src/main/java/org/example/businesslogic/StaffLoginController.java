package org.example.businesslogic;

import org.example.domainmodel.Admin;
import org.example.domainmodel.Maintainer;
import org.example.orm.AdminDAO;
import org.example.orm.MaintainerDAO;

public class StaffLoginController {
    public StaffLoginController() {}

    public Admin adminLogin(String email, String password) {
        AdminDAO adminDAO = new AdminDAO();
        return adminDAO.login(email, password);
    }

    public Maintainer loginMaintainer(String email, String password) {
        MaintainerDAO maintainerDAO = new MaintainerDAO();
        return maintainerDAO.loginMaintainer(email, password);
    }
}
