package org.example.domainmodel;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String message;
    private LocalDateTime dateTime;
    private int idUser;
    private boolean isRead;

    public Notification(int id, String message, LocalDateTime dateTime, boolean isRead, int idUser) {
        this.id = id;
        this.message = message;
        this.dateTime = dateTime;
        this.idUser = idUser;
        this.isRead = isRead;
    }

    public void markAsRead(){
        this.isRead = true;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
