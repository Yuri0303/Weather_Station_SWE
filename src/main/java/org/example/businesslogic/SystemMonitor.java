package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;


public class SystemMonitor extends Thread {

    @Override
    public void run() {
        while (!isInterrupted()) {
            try  {
                DatabaseMutex.mutex.acquire();
                try (SensorDAO sensorDAO = new SensorDAO()) {

                    ArrayList<Sensor> activeSensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
                    for (Sensor s : activeSensors) {
                        boolean sOk = checkSensorValues(s.getId());
                        if (!sOk)
                            openTicket(s.getId());
                    }
                } catch (SQLException e) {
                    System.err.println("Error in SystemMonitor thread: " + e.getMessage());
                } finally {
                    DatabaseMutex.mutex.release();
                }
                Thread.sleep(Duration.ofMinutes(4));
            } catch (InterruptedException e) {
                System.err.println("SystemMonitor interrupted: " + e.getMessage());
                interrupt();
            }
        }
    }

    private boolean checkSensorValues(int sensorId) {
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()) {
            ArrayList<Sensor> sensors = sensorDAO.getSensors(Map.of("id", sensorId));
            Sensor sensor = sensors.getFirst();
            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(Map.of("id", sensor.getLastMeasurementId()));
            if(measurements.isEmpty())
                return true;//se non c'è alcuna misura è come se fosse tutto ok
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

    private void openTicket(int sensorId){
        try (TicketDAO ticketDAO = new TicketDAO(); SensorDAO sensorDAO = new SensorDAO()) {
            ticketDAO.addTicket(sensorId);
            sensorDAO.changeSensorState(sensorId, SensorState.FAULTY);
        } catch (SQLException e) {
            System.err.println("Errore durante l'apertura di un ticket per il sensore " + sensorId);
        }
    }
}
