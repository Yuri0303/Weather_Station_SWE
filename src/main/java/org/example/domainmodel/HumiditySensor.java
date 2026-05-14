package org.example.domainmodel;

public class HumiditySensor extends Sensor {
    public HumiditySensor(int id, SensorType sensorType) {
        super(id, sensorType);
        lowerBound = 25;
        upperBound = 100F;
    }
    public HumiditySensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
        lowerBound = 0.1F;
        upperBound = 100F;
    }
    @Override
    public float measure() {
        double probability = Math.random();
        float value;
        if (probability < 0.80 && sensorState == SensorState.ACTIVE) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //range giusto di umidità tra 0% e 100%
        } else {
            value = (float) (Math.random() * (upperBound + 100)) + (lowerBound - 100);
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
