package org.example.domainmodel;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int idSensor;
    private Integer idMaintainer;
    private boolean isOpen;
    private boolean isTaken;
    private LocalDateTime closeDateTime;

    public Ticket(int id, int idSensor, Integer idMaintainer, boolean isOpen, boolean isTaken, LocalDateTime closeDateTime) {
        this.id = id;
        this.idSensor = idSensor;
        this.idMaintainer = idMaintainer;
        this.isOpen = isOpen;
        this.isTaken = isTaken;
        this.closeDateTime = closeDateTime;
    }

    public int getIdSensor() {
        return idSensor;
    }
}
