package org.example.domainmodel;

public class PressureSensor extends Sensor {

    public PressureSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
    }
    @Override
    public float measure() {
        float lowerBound = 850F;
        float upperBound = 1080F;
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //range giusto di pressione tra 850 e 1080
        } else {
            value = (float) (Math.random() * (upperBound - lowerBound + 520)) + (lowerBound - 350);
        }
        return value;
    }

}
