package org.example.domainmodel;

import java.time.LocalDateTime;

public class Measurement {
    private int id;
    private int sensorId;
    private float value;
    private LocalDateTime dateTime;

    public Measurement(int id, int sensorId, float value, LocalDateTime dateTime) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Float.compare(value, ((Measurement) obj).getValue()) == 0 && dateTime.equals(((Measurement) obj).dateTime);
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "value=" + value +
                ", dateTime=" + dateTime +
                '}';
    }
}
