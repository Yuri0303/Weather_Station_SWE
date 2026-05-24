package org.example.domainmodel;

import java.util.Objects;

public class AlertRule {
    private int id;
    private int userId;
    private Float lowerBound;
    private Float upperBound;
    SensorType sensorType;

    public AlertRule(int id, int userId, Float lowerBound, Float upperBound, SensorType sensorType) {
        this.id = id;
        this.userId = userId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.sensorType = sensorType;
    }

    @Override
    public String toString() {
        return "AlertRule{" +
                "id=" + id +
                ", userId=" + userId +
                ", lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                ", sensorType=" + sensorType +
                '}';
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlertRule other = (AlertRule) obj;
        return this.userId == other.userId &&
                this.sensorType == other.sensorType &&
                java.util.Objects.equals(this.lowerBound, other.lowerBound) &&
                java.util.Objects.equals(this.upperBound, other.upperBound);
    }
    @Override
    public int hashCode() {
        // Generato basandosi sugli stessi campi di equals
        return java.util.Objects.hash(userId, lowerBound, upperBound, sensorType);
    }
}
