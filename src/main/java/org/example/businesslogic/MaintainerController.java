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

    public void closeTicket(int ticketId) throws SQLException{
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.closeTicket(ticketId);
        }catch (SQLException e){
            System.err.println("Errore durante la chiusura del ticket" + e.getMessage());
        }
    }


    public void changeSensor(int ticketId){
        try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);

            Sensor changingSensor;
            Map<String , Object> map = new HashMap<>();
            map.put("id",sensorId);
            ArrayList<Sensor> sensors = sensorDAO.getSensors(map);

            changingSensor = sensors.getFirst();

            sensorDAO.changeSensorState(sensorId, SensorState.DEACTIVATED);
            sensorDAO.addSensor(changingSensor.getSensorType());
            closeTicket(ticketId);

        }catch (SQLException e){
            System.err.println("La sostituazione del sensore da parte identificato dal ticketId " + ticketId + " non è andata a buon fine");
            e.getStackTrace();
        }
    }


    public String repairSensor(double repairOrChange, int ticketId){//la probabilità viene passata come parameetro altrimenti risulta troppo difficile da testare
        if(repairOrChange < 0.88){

            try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
                Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId);
                if (sensorId == null) throw new SQLException("Sensore non trovato");//fixme è possibile?

                sensorDAO.changeSensorState(sensorId, SensorState.ACTIVE);
                closeTicket(ticketId);
                System.out.println("il sensore è stato riparato");
                return "sensore_riparato";

            } catch (SQLException e) {
                System.err.println("Errore: transazione annullata per ticket " + ticketId);
                e.getStackTrace();
            }

        }else{
            changeSensor(ticketId);
            System.out.println("Il sensore è stato sostituito");
            return "sensore_sostituito";
        }
            return "errore-generale";
    }

}
