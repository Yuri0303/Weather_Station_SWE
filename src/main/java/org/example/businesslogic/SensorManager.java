package org.example.businesslogic;

import org.example.domainmodel.Measurement;
import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

//FIXME: Cambiare implementazione Observer, mettendo SensorManager come Observable? (guardare chat discord)
public class SensorManager extends Thread {
    //TODO: sincronizza con semaforo così che solo un thread alla volta può accedere al database
    //private SharedListActiveSensors sharedListActiveSensors = new SharedListActiveSensors();

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                DatabaseMutex.mutex.acquire();
                try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){
                    //FIXME: Usare direttamente i DAO
                    //ArrayList<Sensor> activeSensors = sharedListActiveSensors.getActualActiveSensors();

                    ArrayList<Sensor> activeSensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);

                    for(Sensor s : activeSensors){
                        float newMeasure = s.measure();
                        int lastMeasurementId = measurementDAO.addMeasurement(createMeasurement(newMeasure, s.getId()));
                        sensorDAO.updateLastMeasurement(s.getId(), lastMeasurementId);
                        s.notifyObservers();
                    }
                } catch (SQLException e) {
                    System.err.println("Errore durante la registrazione di nuove misure - Errore del sensor manager");
                    e.getStackTrace();
                } finally {
                    DatabaseMutex.mutex.release();
                }
                sleep(Duration.ofMinutes(3));
            } catch (InterruptedException e) {
                System.err.println("SensorManager interrupted: " + e.getMessage());
                interrupt();
            }
        }
    }

    private Measurement createMeasurement(float value, int sensorId) {
        return new Measurement(0, sensorId, value, LocalDateTime.now()); //FIXME: id fittizio, poi lo impostare il DBMS tramite MeasurementDAO
    }
}
