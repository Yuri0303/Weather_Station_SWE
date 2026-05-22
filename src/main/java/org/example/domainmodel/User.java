package org.example.domainmodel;

import java.util.ArrayList;

public class User extends SystemUser {

    private boolean isBlocked;
    private ArrayList<AlertRule> alertRules = new ArrayList<>();

    public User(int id, String firstname, String lastName, String email, boolean isBlocked) {
        super(id, firstname, lastName, email);
        this.isBlocked = isBlocked;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return email.equals(((User) obj).email); //basta la email, visto che è unica
    }

    public boolean isBlocked() {
        return isBlocked;
    }
}
