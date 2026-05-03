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

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
