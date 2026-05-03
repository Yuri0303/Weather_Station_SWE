package org.example.domainmodel;

import java.util.ArrayList;

public class User extends SystemUser {

    private boolean isBlocked;
    private ArrayList<AlertRule> alertRules = new ArrayList<>();

    User(int id, String firstname, String lastName, String email) {
        super(id, firstname, lastName, email);
        isBlocked = false;
    }

    public void createAlertRule(){
        //puzzo
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public ArrayList<AlertRule> getAlertRules() {
        return alertRules;
    }

    public void setAlertRules(ArrayList<AlertRule> alertRules) {
        this.alertRules = alertRules;
    }
}
