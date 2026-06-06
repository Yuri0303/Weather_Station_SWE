package org.example.domainmodel;

public abstract class Sensor {
    protected int id;
    protected Integer lastMeasurementId;
    protected SensorType sensorType;
    protected SensorState sensorState;

    public Sensor(int id, Integer lastMeasurementId, SensorType sensorType, SensorState sensorState) {
        this.id = id;
        this.lastMeasurementId = lastMeasurementId;
        this.sensorType = sensorType;
        this.sensorState = sensorState;
    }

    public int getId(){
        return id;
    }

    public abstract float measure();

    public SensorType getSensorType(){
        return sensorType;
    }

    public SensorState getSensorState(){return sensorState;}

    public Integer getLastMeasurementId(){return lastMeasurementId;}

    public void sensorStateToFaulty(){this.sensorState = SensorState.FAULTY;}//usata solo nei test

}
