package org.example.domainmodel;

public class TemperatureSensor extends Sensor {

    public TemperatureSensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        super(id, idLastMeasurement, sensorType, sensorState);
    }

    @Override
    public float measure() {
        float lowerBound = 0F;
        float upperBound = 35F;
        double probability = Math.random();
        float value;
        if (probability < 0.80) {
            value = (float) (Math.random() * (upperBound - lowerBound)) + lowerBound;  //temperatura giusta nel range tra 0 e 35 gradi
        } else {
            value = (float) (Math.random() * (upperBound - lowerBound + 65)) + (lowerBound - 20);
        }
        return value;
    }

}
