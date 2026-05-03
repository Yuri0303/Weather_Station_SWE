package org.example.domainmodel;

public interface Observer {
    public void update(int measurementId, SensorType sensorType);
}
