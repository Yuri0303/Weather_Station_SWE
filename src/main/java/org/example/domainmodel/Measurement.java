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

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
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

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
