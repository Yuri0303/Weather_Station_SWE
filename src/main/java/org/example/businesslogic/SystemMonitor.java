package org.example.businesslogic;

import org.example.domainmodel.Measurement;
import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.orm.DatabaseManager;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SystemMonitor {

    //todo c'è da decidere, anche in virtù del fatto che ci sono le transazioni, se fare il controllo su un singolo sensore con sensorId o se farlo su tutti gli ACTIVE

    //fixme l'ho sviluppata in modo che agisca su tutti i sensori, come la teniamo?
    // Il boolean era per "Se va tutto bene è true, altrimenti false?", caso forse è meglio toglierlo?
    public boolean checkSensorActivity(int sensorId, int boundDays){//l'ultima data di misurazione non dev'essere troppo vecchia
        Connection conn = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            SensorDAO sensorDAO = new SensorDAO(conn);
            MeasurementDAO measurementDAO = new MeasurementDAO(conn);

            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            for (Sensor it : sensors){
                int idLastMeasurement = it.getIdLastMeasurement();
                map.put("id", idLastMeasurement);//mappa che contiene tutti gli id delle ultime misurazioni di tutti i sensori attivi. Potrebbero essere nulle
            }

            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(map);

            for (Measurement it : measurements){
                long pastDays = ChronoUnit.DAYS.between(it.getDateTime(), LocalDateTime.now());
                if(pastDays > boundDays){
                    sensorDAO.changeSensorState(it.getSensorId(), SensorState.FAULTY);
                    openTicket(it.getSensorId());//Se più vecchia del limite dei giorni potrebbe esserci un problema, quinid viene aperto un ticket per quel sensore
                }
            }
            conn.commit();

        }catch (SQLException | ClassNotFoundException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }

            System.err.println("Errore: transazione annullata per controllo della data dell'ultima misurazione dei sensori dei sensori ");
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

    }

    //fixme ho aggiunto i parametri upperBound e lowerBound a ogni tipo di sensore, perché altrimenti non ho parametri di riferimento, forse non importa metterli nel db, tanto sono uguali e costanti per ogni sensore?
    // fixme anche questa la sviluppo in modo che controlli tutti i sensori ACTIVE
    public boolean checkSensorValues(int sensorId){
        Connection conn = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            SensorDAO sensorDAO = new SensorDAO(conn);
            MeasurementDAO measurementDAO = new MeasurementDAO(conn);

            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            for (Sensor it : sensors){
                int idLastMeasurement = it.getIdLastMeasurement();
                map.put("id", idLastMeasurement);//mappa che contiene tutti gli id delle ultime misurazioni di tutti i sensori attivi. Potrebbero essere nulle
            }

            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(map);

            //fixme questa è da modificare se si mettono i valori limite dei sensori nel database
            for (Measurement it : measurements){
                Map<String, Object> mapId = new HashMap<>();
                mapId.put("id", it.getSensorId());
                Sensor targetSensor = sensorDAO.getSensors(mapId).getFirst();

                if(it.getValue() > targetSensor.getUpperBound() || it.getValue() < targetSensor.getLowerBound()){
                    sensorDAO.changeSensorState(it.getSensorId(), SensorState.FAULTY);
                    openTicket(it.getSensorId());
                }
            }
            conn.commit();
        }catch (SQLException | ClassNotFoundException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }

            System.err.println("Errore: transazione annullata per controllo dei valori dei sensori ");
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

    }

    public void openTicket(int sensorId){
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(sensorId);
        }catch (SQLException e){
            System.err.println("Errore durante l'apertura di un ticket per il sensore " + sensorId);
        }
    }
}
