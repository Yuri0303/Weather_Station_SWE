package org.example.businesslogic;

import org.example.domainmodel.Measurement;
import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.domainmodel.SensorType;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SensorManager extends Observable implements Runnable {

    public SensorManager(){
        this.observers = new ArrayList<>();
    }

    @Override
    public void notifyObservers(int lastMeasurementId, SensorType sensorType) {
        for (Observer o : observers) {
            o.update(lastMeasurementId, sensorType);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DatabaseMutex.mutex.acquire();
                try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){

                    ArrayList<Sensor> activeSensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);

                    for(Sensor s : activeSensors){
                        float newMeasure = s.measure();
                        int lastMeasurementId = measurementDAO.addMeasurement(createMeasurement(newMeasure, s.getId()));
                        sensorDAO.updateLastMeasurement(s.getId(), lastMeasurementId);
                        notifyObservers(lastMeasurementId, s.getSensorType());
                    }
                } catch (SQLException e) {
                    System.err.println("Errore durante la registrazione di nuove misure - Errore del sensor manager");
                    e.getStackTrace();
                } finally {
                    DatabaseMutex.mutex.release();
                }
                Thread.sleep(Duration.ofMinutes(3));
            } catch (InterruptedException e) {
                System.err.println("SensorManager interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private Measurement createMeasurement(float value, int sensorId) {
        return new Measurement(0, sensorId, value, LocalDateTime.now()); //id fittizio
    }
}
