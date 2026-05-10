package org.example.domainmodel;

public class PressureSensor extends Sensor {
    public PressureSensor(int id, SensorType sensorType) {
        super(id, sensorType);
    }
    public PressureSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
    }
    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * 230) + 850;  //range giusto di pressione tra 850 e 1080
        } else {
            value = (float) (Math.random() * 750) + 500;
        }
        return value;
    }
}
