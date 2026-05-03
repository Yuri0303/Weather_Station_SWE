package org.example.domainmodel;

public class WindSensor extends Sensor {
    public WindSensor(int id, SensorType sensorType, SensorState sensorState) {
        super(id, sensorType, sensorState);
    }

    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * 55);  //range giusto di umidità tra 0% e 100%
        } else {
            value = (float) (Math.random() * 180) - 10;
        }
        return value;
    }
}
