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
        TicketDAO ticketDAO = new TicketDAO();
        return ticketDAO.getOpenTickets();
    }

    public void takeTicket(int ticketId, int maintainerId){
        TicketDAO ticketDAO = new TicketDAO();
        try {
            ticketDAO.takeTicket(ticketId, maintainerId);
        }catch (SQLException | RuntimeException e){
            System.err.println("L'acquisizione del ticket da parte di maintainer " + maintainerId + "non è andata a buon fine");
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
        Connection conn = null;
        boolean success = false;
        try {
            // Recuperiamo la connessione unica per questa operazione
            conn = DatabaseManager.getInstance().getConnection();

            // DISABILITIAMO l'auto-commit per iniziare la transazione
            conn.setAutoCommit(false);

            // Passiamo la STESSA connessione a entrambi i DAO
            SensorDAO sensorDAO = new SensorDAO(conn);
            TicketDAO ticketDAO = new TicketDAO(conn);

            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);
            if (sensorId == null) throw new SQLException("Sensore non trovato");
            Map<String, Object> map = new HashMap<>();
            map.put("id", sensorId);
            ArrayList<Sensor> sensors = sensorDAO.getSensors(map);
            Sensor changingSensor = sensors.getFirst();



            // Queste due operazioni ora sono "congelate" fino al commit
            sensorDAO.changeSensorState(sensorId, SensorState.DEACTIVATED);
            sensorDAO.addSensor(changingSensor.getSensorType());
            closeTicket(ticketId, maintainerId, conn);

            // SE arriviamo qui senza errori, salviamo tutto definitivamente
            conn.commit();
            success = true;

        } catch (SQLException | ClassNotFoundException e) {
            // SE c'è un errore, annulliamo tutto (il sensore non viene disattivato)
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }

            System.err.println("Errore: transazione annullata per ticket " + ticketId);
        } finally {
            // Importante: riportiamo la connessione allo stato normale e la chiudiamo
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

            try {
                conn = DatabaseManager.getInstance().getConnection();
                conn.setAutoCommit(false);

                SensorDAO sensorDAO = new SensorDAO(conn);
                TicketDAO ticketDAO = new TicketDAO(conn);

                Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);
                if (sensorId == null) throw new SQLException("Sensore non trovato");

                sensorDAO.changeSensorState(sensorId, SensorState.ACTIVE);
                closeTicket(ticketId, maintainerId, conn);

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

    public void closeTicket(int ticketId, int maintainerId, Connection conn) throws SQLException{ //FIXME potrebbe non servire il maitainerId?
        TicketDAO ticketDAO = new TicketDAO(conn); //è una funzione che viene chiamata all'interno di una transazione, quindi la connessione deve essere la solita

        ticketDAO.closeTicket(ticketId, maintainerId);
    }
}
