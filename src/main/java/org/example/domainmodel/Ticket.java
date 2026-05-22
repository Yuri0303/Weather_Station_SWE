package org.example.domainmodel;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private int id;
    private int sensorId;
    private Integer maintainerId;
    private boolean isOpen;
    private boolean isTaken;
    private LocalDateTime closeDateTime;

    public Ticket(int id, int sensorId, Integer maintainerId, boolean isOpen, boolean isTaken, LocalDateTime closeDateTime) {
        this.id = id;
        this.sensorId = sensorId;
        this.maintainerId = maintainerId;
        this.isOpen = isOpen;
        this.isTaken = isTaken;
        this.closeDateTime = closeDateTime;
    }

    public int getSensorId() {
        return sensorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return sensorId == ticket.sensorId && isOpen == ticket.isOpen && isTaken == ticket.isTaken && Objects.equals(maintainerId, ticket.maintainerId) && Objects.equals(closeDateTime, ticket.closeDateTime);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "closeDateTime=" + closeDateTime +
                ", id=" + id +
                ", sensorId=" + sensorId +
                ", maintainerId=" + maintainerId +
                ", isOpen=" + isOpen +
                ", isTaken=" + isTaken +
                '}';
    }
}
