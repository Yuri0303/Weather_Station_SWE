package org.example.domainmodel;

public class HumiditySensor extends Sensor {

    public HumiditySensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
    }
    @Override
    public float measure() {
        float lowerBound = 0F;
        float upperBound = 100F;
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //range giusto di umidità tra 0% e 100%
        } else {
            value = (float) (Math.random() * (upperBound + 100)) + (lowerBound - 100);
        }
        return value;
    }

}
