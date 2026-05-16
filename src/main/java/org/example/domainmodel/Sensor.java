package org.example.domainmodel;

public abstract class Sensor extends Observable {
    protected int id;
    protected Integer idLastMeasurement;
    protected SensorType sensorType;
    protected SensorState sensorState;

    public Sensor(int id, SensorType sensorType) {//fixme si tiene questa modifica?
        this.id = id;
        this.idLastMeasurement = null;
        this.sensorType = sensorType;
        this.sensorState = SensorState.ACTIVE;
    }

    public Sensor(int id, int idLastMeasurement, SensorType sensorType, SensorState sensorState) {
        this.id = id;
        this.idLastMeasurement = idLastMeasurement;
        this.sensorType = sensorType;
        this.sensorState = sensorState;
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(idLastMeasurement, sensorType);
        }
    }

    public abstract float measure();

    public SensorType getSensorType(){
        return sensorType;
    }

    public Integer getIdLastMeasurement(){return idLastMeasurement;}

}
