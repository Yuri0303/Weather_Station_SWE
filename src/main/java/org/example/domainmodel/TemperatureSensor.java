package org.example.domainmodel;

import java.time.LocalDateTime;

public class TemperatureSensor extends Sensor {

    public TemperatureSensor(int id, SensorType sensorType, SensorState sensorState) {
        super(id, sensorType, sensorState);
    }

    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80) {
            value = (float) (Math.random() * 35);  //temperatura giusta nel range tra 0 e 35 gradi
        } else {
            value = (float) (Math.random() * 100) - 20;
        }
        return value;
    }

}
