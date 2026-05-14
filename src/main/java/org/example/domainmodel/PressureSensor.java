package org.example.domainmodel;

public class PressureSensor extends Sensor {
    public PressureSensor(int id, SensorType sensorType) {
        super(id, sensorType);
        lowerBound = 850F;
        upperBound = 1080F;
    }
    public PressureSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
        lowerBound = 850F;
        upperBound = 1080F;
    }
    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //range giusto di pressione tra 850 e 1080
        } else {
            value = (float) (Math.random() * (upperBound - lowerBound + 520)) + (lowerBound - 350);
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
