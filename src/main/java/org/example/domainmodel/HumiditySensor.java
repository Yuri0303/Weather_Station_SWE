package org.example.domainmodel;

public class HumiditySensor extends Sensor {
    public HumiditySensor(int id, SensorType sensorType) {
        super(id, sensorType);
    }
    public HumiditySensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
    }
    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * 100);  //range giusto di umidità tra 0% e 100%
        } else {
            value = (float) (Math.random() * 200) - 100;
        }
        return value;
    }
}
