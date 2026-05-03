package org.example.domainmodel;

public class Notification {
    private int id;
    private int idUser;
    private String message;
    private boolean isRead;

    public Notification(int id, int idUser, String message) {
        this.id = id;
        this.idUser = idUser;
        this.message = message;
        this.isRead = false;
    }

    public void markAsRead(){
        this.isRead = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
