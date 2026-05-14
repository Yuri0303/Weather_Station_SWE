package org.example.domainmodel;

public class WindSensor extends Sensor {
    public WindSensor(int id, SensorType sensorType) {
        super(id, sensorType);
        lowerBound = 0F;
        upperBound = 55F;
    }
    public WindSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
        lowerBound = 0F;
        upperBound = 55F;
    }
    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //range giusto di velocità del vento tra 0 e 55
        } else {
            value = (float) (Math.random() * (upperBound - lowerBound + 125)) + (lowerBound - 10);
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
