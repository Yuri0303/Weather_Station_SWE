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


}
