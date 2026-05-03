package org.example.domainmodel;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int idSensor;
    private Integer idMaintainer;
    private boolean isOpen;
    private boolean isTaken;
    private LocalDateTime closeDateTime;

    public Ticket(int id, int idSensor, LocalDateTime closeDateTime) {
        this.id = id;
        this.idSensor = idSensor;
        this.idMaintainer = null;
        this.isOpen = true;
        this.isTaken = false;
        this.closeDateTime = closeDateTime;
    }

    public void take(int idMaintainer){
        this.idMaintainer = idMaintainer;
        this.isTaken = true;
    }

    public void close(){
        this.isOpen = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public Integer getIdMaintainer() {
        return idMaintainer;
    }

    public void setIdMaintainer(Integer idMaintainer) {
        this.idMaintainer = idMaintainer;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public LocalDateTime getCloseDateTime() {
        return closeDateTime;
    }

    public void setCloseDateTime(LocalDateTime closeDateTime) {
        this.closeDateTime = closeDateTime;
    }
}
