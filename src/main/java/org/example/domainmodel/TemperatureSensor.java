package org.example.domainmodel;

import java.time.LocalDateTime;

public class TemperatureSensor extends Sensor {

    public TemperatureSensor(int id, SensorType sensorType) {
        super(id, sensorType);
        lowerBound = 0F;
        upperBound = 35F;
    }

    public TemperatureSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
        lowerBound = 0F;
        upperBound = 35F;
    }

    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //temperatura giusta nel range tra 0 e 35 gradi
        } else {
            value = (float) (Math.random() * (upperBound - lowerBound + 65)) + (lowerBound - 20);
        }
        return value;
    }

@Override
    public float getLowerBound() {
        return lowerBound;
    }
@Override
    public float getUpperBound() {
        return upperBound;
    }
}
