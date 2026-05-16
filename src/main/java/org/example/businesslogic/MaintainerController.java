package org.example.businesslogic;

import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.domainmodel.Ticket;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import org.example.orm.DatabaseManager;

public class MaintainerController {
    public ArrayList<Ticket> viewOpenTickets(){
        try (TicketDAO ticketDAO = new TicketDAO()) {
            return ticketDAO.getOpenTickets();
        }catch (SQLException e){
            System.err.println("Errore durante la lettura dei ticket aperti" + e.getMessage());
            return null;
        }

    }

    public void takeTicket(int ticketId, int maintainerId){
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.takeTicket(ticketId, maintainerId);
        }catch (SQLException | RuntimeException e){
            System.err.println("L'acquisizione del ticket da parte di maintainer " + maintainerId + "non è andata a buon fine" + e.getMessage());
            e.getStackTrace();
        }
    }

//Versione senza la transazione
    /*public void changeSensor(int ticketId){
        SensorDAO sensorDAO = new SensorDAO();
        TicketDAO ticketDAO = new TicketDAO();

        try {
            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);

            Sensor changingSensor;
            Map<String , Object> map = new HashMap<>();
            map.put("id",sensorId);
            ArrayList<Sensor> sensors = sensorDAO.getSensors(map);

            changingSensor = sensors.getFirst();

            sensorDAO.changeSensorState(sensorId, SensorState.DEACTIVATED);
            sensorDAO.addSensor(changingSensor.getSensorType());
        }catch (SQLException e){
            System.err.println("La sostituazione del sensore da parte identificato dal ticketId " + ticketId + " non è andata a buon fine");
            e.getStackTrace();
        }
    }*/

    public boolean changeSensor(int ticketId, int maintainerId) {
        boolean success = false;
        try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
            conn.setAutoCommit(false);


            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);
            if (sensorId == null) throw new SQLException("Sensore non trovato");
            Map<String, Object> map = new HashMap<>();
            map.put("id", sensorId);
            ////////////////////////////todo fondere le funzioni
            ArrayList<Sensor> sensors = sensorDAO.getSensors(map);
            Sensor changingSensor = sensors.getFirst();


            sensorDAO.changeSensorState(sensorId, SensorState.DEACTIVATED);
            ///////////////////////////
            sensorDAO.addSensor(changingSensor.getSensorType());
            closeTicket(ticketId, maintainerId);


            conn.commit();
            success = true;

        } catch (SQLException | ClassNotFoundException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }

            System.err.println("Errore: transazione annullata per ticket " + ticketId);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

        return success;
    }

    public boolean repairSensor(int ticketId, int maintainerId){
        Connection conn = null;
        double repairOrChange = Math.random();
        boolean success = false;

        if(repairOrChange < 0.88){

            try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
                conn = DatabaseManager.getConnection();
                conn.setAutoCommit(false);



                Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);
                if (sensorId == null) throw new SQLException("Sensore non trovato");

                sensorDAO.changeSensorState(sensorId, SensorState.ACTIVE);
                closeTicket(ticketId, maintainerId);

                conn.commit();
                success = true;

            } catch (SQLException | ClassNotFoundException e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }

                System.err.println("Errore: transazione annullata per ticket " + ticketId);
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) { e.printStackTrace(); }
            }

        }else
            success = changeSensor(ticketId, maintainerId);

        return success;
    }

    public void closeTicket(int ticketId, int maintainerId) throws SQLException{ //FIXME potrebbe non servire il maitainerId?
        try (TicketDAO ticketDAO = new TicketDAO()) {//è una funzione che viene chiamata all'interno di una transazione, quindi la connessione deve essere la solita
            ticketDAO.closeTicket(ticketId, maintainerId);
        }catch (SQLException e){
            System.err.println("Errore durante la chiusura del ticket" + e.getMessage());
        }
    }
}
