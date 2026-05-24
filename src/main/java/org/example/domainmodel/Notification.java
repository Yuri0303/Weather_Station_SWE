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
        this.isRead = isRead;
        this.idUser = idUser;
    }
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", dateTime=" + dateTime +
                ", isRead=" + isRead +
                ", idUser=" + idUser +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;

        return id == that.id &&
                isRead == that.isRead &&
                idUser == that.idUser &&
                java.util.Objects.equals(message, that.message) &&
                // Confrontiamo le date ignorando eventuali rimasugli di nano/milli
                java.util.Objects.equals(
                        dateTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                        that.dateTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
                );
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, message, dateTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS), isRead, idUser);
    }
}
