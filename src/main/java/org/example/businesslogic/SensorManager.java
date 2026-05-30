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

public class SensorManager extends Thread {
    //TODO: sincronizza con semaforo così che solo un thread alla volta può accedere al database
    private SharedListActiveSensors sharedListActiveSensors = new SharedListActiveSensors();

    @Override
    public void run() {
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){

            sharedListActiveSensors.acquireMutex();
            ArrayList<Sensor> activeSensors = sharedListActiveSensors.getActualActiveSensors();

            //ArrayList<Sensor> activeSensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            for(Sensor s : activeSensors){
                float newMeasure = s.measure();
                int lastMeasurementId = measurementDAO.addMeasurement(createMeasurement(newMeasure, s.getId()));
                sensorDAO.updateLastMeasurement(s.getId(), lastMeasurementId);
                s.notifyObservers();
            }

            sharedListActiveSensors.releaseMutex();
            sleep(Duration.ofMinutes(3));
        } catch (SQLException e) {
            System.err.println("Errore durante la registrazione di nuove misure - Errore del sensor manager");
            e.getStackTrace();
        } catch (InterruptedException e) {
            System.out.println("SensorManager interrupted");
        }
    }

    private Measurement createMeasurement(float value, int sensorId) {
        return new Measurement(0, sensorId, value, LocalDateTime.now()); //FIXME: id fittizio, poi lo impostare il DBMS tramite MeasurementDAO
    }
}
