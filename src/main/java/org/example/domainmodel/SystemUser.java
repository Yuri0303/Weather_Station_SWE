package org.example.domainmodel;

public abstract class SystemUser {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String email;

    SystemUser(int id, String firstname, String lastName, String email){
        this.id=id;
        this.firstName=firstname;
        this.lastName=lastName;
        this.email=email;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return email.equals(((SystemUser) obj).email); //basta la email, visto che è unica
    }
}
