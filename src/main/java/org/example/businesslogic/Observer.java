package org.example.businesslogic;

import org.example.domainmodel.SensorType;

public interface Observer {
    public void update(int measurementId, SensorType sensorType);
}
