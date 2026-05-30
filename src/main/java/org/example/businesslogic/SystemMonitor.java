package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class SystemMonitor extends Thread{

    //TODO: metodo run di thread, dove si prende la lista dei sensori ACTIVE e nel caso apre i ticket
    //TODO: sincronizza con semaforo così che solo un thread alla volta può accedere al database

    private SharedListActiveSensors sharedListActiveSensors = new SharedListActiveSensors();

    @Override
    public void run(){
        try {
            sharedListActiveSensors.acquireMutex();
            ArrayList<Sensor> activeSensors = sharedListActiveSensors.getActualActiveSensors();
            for(Sensor s : activeSensors){
                boolean sOk = checkSensorValues(s.getId());
                if(!sOk)
                    openTicket(s.getId());
            }
            sharedListActiveSensors.releaseMutex();
        }catch (InterruptedException e){

        }catch (SQLException e){

        }

    }

    private boolean checkSensorValues(int sensorId) {
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()) {
            ArrayList<Sensor> sensors = sensorDAO.getSensors(Map.of("id", sensorId));
            Sensor sensor = sensors.getFirst();
            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(Map.of("id", sensor.getLastMeasurementId()));
            Measurement measurement = measurements.getFirst();
            if (sensor instanceof TemperatureSensor) {
                float lowerBound = 0F;
                float upperBound = 35F;
                if (measurement.getValue() < lowerBound || measurement.getValue() > upperBound) {
                    return false;
                }
            } else if (sensor instanceof HumiditySensor) {
                float lowerBound = 0F;
                float upperBound = 100F;
                if (measurement.getValue() < lowerBound || measurement.getValue() > upperBound) {
                    return false;
                }
            } else if (sensor instanceof PressureSensor) {
                float lowerBound = 850F;
                float upperBound = 1080F;
                if (measurement.getValue() < lowerBound || measurement.getValue() > upperBound) {
                    return false;
                }
            } else if (sensor instanceof WindSensor) {
                float lowerBound = 0F;
                float upperBound = 55F;
                if (measurement.getValue() < lowerBound || measurement.getValue() > upperBound) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during checkSensorValue" + e.getMessage());
            e.getStackTrace();
        }
        return true;
    }

    private void openTicket(int sensorId){//fixme l'ho reso privato, tanto è usato solo qui
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.addTicket(sensorId);
        } catch (SQLException e) {
            System.err.println("Errore durante l'apertura di un ticket per il sensore " + sensorId);
        }
    }
}
