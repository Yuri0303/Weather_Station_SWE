package org.example.businesslogic;

import org.example.domainmodel.SensorType;

public interface Observer {
    void update(int measurementId, SensorType sensorType);
}
