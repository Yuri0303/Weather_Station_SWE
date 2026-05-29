package org.example.domainmodel;

public abstract class Sensor extends Observable {
    protected int id;
    protected Integer lastMeasurementId;
    protected SensorType sensorType;
    protected SensorState sensorState;

    public Sensor(int id, SensorType sensorType) {//fixme si tiene questa modifica?
        this.id = id;
        this.lastMeasurementId = null;
        this.sensorType = sensorType;
        this.sensorState = SensorState.ACTIVE;
    }

    public Sensor(int id, Integer lastMeasurementId, SensorType sensorType, SensorState sensorState) {
        this.id = id;
        this.lastMeasurementId = lastMeasurementId;
        this.sensorType = sensorType;
        this.sensorState = sensorState;
    }

    @Override   //TODO questa funzioanlità di notifica è stata implementata ma mai usata. Avevemo fatto qualcosa di diverso? Perché se sì l'obsterver non è mai usato all'interno del codice. Inoltre, per come è stato impostato, è responsabilità del sensore notificare il SensorMonitor, va bene?
                // FIXME credo che vada chiamata all'interno della funzione che esegue le misurazioni o in SystemMonitor.checkSensorValues()
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(lastMeasurementId, sensorType);
        }
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
