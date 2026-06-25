package org.example.domainmodel;

public class User extends SystemUser {

    private boolean isBlocked;

    public User(int id, String firstname, String lastName, String email, boolean isBlocked) {
        super(id, firstname, lastName, email);
        this.isBlocked = isBlocked;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public String toString() {
        return String.format("%d)\t %-30s %-30s %-50s %s", id, firstName, lastName, email, isBlocked ? "Sì" : "No");
    }
}
