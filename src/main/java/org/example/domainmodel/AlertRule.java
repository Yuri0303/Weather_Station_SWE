package org.example.domainmodel;

public class AlertRule {
    private int id;
    private int userId;
    private float lowerBound;
    private float upperBound;
    SensorType sensorType;

    public AlertRule(int id, int userId, float lowerBound, float upperBound, SensorType sensorType) {
        this.id = id;
        this.userId = userId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.sensorType = sensorType;
    }

    public boolean isViolatedBy(Measurement m) {
        return m.getValue() < lowerBound || m.getValue() > upperBound;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }
}
