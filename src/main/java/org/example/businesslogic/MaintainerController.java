package org.example.businesslogic;

import org.example.domainmodel.Maintainer;
import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.domainmodel.Ticket;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaintainerController {
    public ArrayList<Ticket> viewOpenTickets(){
        try (TicketDAO ticketDAO = new TicketDAO()) {
            return ticketDAO.getOpenTickets();
        }catch (SQLException e){
            System.err.println("Errore durante la lettura dei ticket aperti" + e.getMessage());
            return null;
        }

    }

    public void takeTicket(int ticketId, int maintainerId) throws SQLException {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.takeTicket(ticketId, maintainerId);
        } catch (SQLException e) {
            System.err.println("L'acquisizione del ticket da parte di maintainer " + maintainerId + " non è andata a buon fine: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }

    public void closeTicket(int ticketId, int maintainerId) throws SQLException {
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.closeTicket(ticketId, maintainerId);
        }catch (SQLException e){
            System.err.println("Errore durante la chiusura del ticket" + e.getMessage());
            throw e;
        }
    }

    public void changeSensor(int ticketId, int maintainerId) throws RuntimeException {
        try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId, maintainerId);
            if (sensorId == null) {
                throw new RuntimeException("Ticket non mio oppure ticket già chiuso");
            }
            Sensor changingSensor;
            Map<String , Object> map = new HashMap<>();
            map.put("id",sensorId);
            ArrayList<Sensor> sensors = sensorDAO.getSensors(map);

            changingSensor = sensors.getFirst();

            sensorDAO.changeSensorState(sensorId, SensorState.DEACTIVATED);
            Integer lastID = sensorDAO.addSensor(changingSensor.getSensorType());//mi deve restituire l'ultimo sensore inserito in qualche modo, all'ultimo sensore inserito farò l'attach

            if(lastID == null)
                throw new RuntimeException("Errore nell'inserimento di un nuovo sensore in seguito alla disattivazione di un altro");

            closeTicket(ticketId, maintainerId);

        }catch (SQLException e){
            System.err.println("La sostituzione del sensore da parte identificato dal ticketId " + ticketId + " non è andata a buon fine");
            e.getStackTrace();
        }
    }

    public String repairSensor(double repairChance, int ticketId, int maintainerId){  //la probabilità viene passata come parametro altrimenti risulta troppo difficile da testare
        if(repairChance < 0.88) {

            try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
                Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId, maintainerId);
                if (sensorId == null) throw new SQLException("Sensore non trovato");

                sensorDAO.changeSensorState(sensorId, SensorState.ACTIVE);
                closeTicket(ticketId, maintainerId);
                System.out.println("il sensore è stato riparato");
                return "sensore_riparato";

            } catch (SQLException e) {
                System.err.println("Errore: transazione annullata per ticket " + ticketId);
                e.getStackTrace();
            }

        } else {
            try {
                changeSensor(ticketId, maintainerId);
                System.out.println("Il sensore è stato sostituito");
                return "sensore_sostituito";
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                return "errore-generale";
            }
        }
            return "errore-generale";
    }

}
