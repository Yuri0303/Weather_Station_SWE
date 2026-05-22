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

    public LocalDateTime getDateTime() {
        return dateTime;
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

    public String getMessage() {
        return message;
    }
}
