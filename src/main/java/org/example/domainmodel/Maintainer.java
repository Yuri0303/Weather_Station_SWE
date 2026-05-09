package org.example.domainmodel;

public class Maintainer extends SystemUser{
    private Integer ticketTaken;

    public Maintainer(int id, String firstname, String lastName, String email) {
        super(id, firstname, lastName, email);
        ticketTaken = null;
    }

    public void takeTicket(){}

    public void closeTicket() {

    }

    public void repairSensor() {

    }

    public void changeSensor() {

    }

    public Integer getTicketTaken() {
        return ticketTaken;
    }

    public void setTicketTaken(Integer ticketTaken) {
        this.ticketTaken = ticketTaken;
    }
}

