package org.example.domainmodel;

public class AlertRule {
    private int id;
    private int userId;
    private Float lowerBound;
    private Float upperBound;
    SensorType sensorType;

    public AlertRule(int id, int userId, float lowerBound, float upperBound, SensorType sensorType) {
        this.id = id;
        this.userId = userId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.sensorType = sensorType;
    }

    public boolean isViolatedBy(Measurement m) {
        boolean violated = false;
        if (lowerBound != null && m.getValue() < lowerBound) {
            violated = true;
        } else if (upperBound != null && m.getValue() > upperBound) {
            violated = true;
        }
        return violated;
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

    public float getLowerBound() {
        return lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }
}
