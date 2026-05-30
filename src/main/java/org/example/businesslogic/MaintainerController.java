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

//TODO: Manca da sistemare questo. Perché changeOrRepair come parametro?    |   L'observer non funziona bene (guardare discord)
public class MaintainerController {
    private final Maintainer maintainer;

    public MaintainerController(Maintainer maintainer) {
        this.maintainer = maintainer;
    }

    public ArrayList<Ticket> viewOpenTickets(){
        try (TicketDAO ticketDAO = new TicketDAO()) {
            return ticketDAO.getOpenTickets();
        }catch (SQLException e){
            System.err.println("Errore durante la lettura dei ticket aperti" + e.getMessage());
            return null;
        }

    }

    public void takeTicket(int ticketId) throws SQLException {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.takeTicket(ticketId, maintainer.getId());
        } catch (SQLException e) {
            System.err.println("L'acquisizione del ticket da parte di maintainer " + maintainer.getId() + " non è andata a buon fine: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }

    public void closeTicket(int ticketId) throws SQLException {
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.closeTicket(ticketId, maintainer.getId());
        }catch (SQLException e){
            System.err.println("Errore durante la chiusura del ticket" + e.getMessage());
            throw e;
        }
    }


    //todo nei test si deve inserire i sensorMonitor. NOTA: ho assunto che possano esere anche più di uno per rendere il programma più flessibile
    public void changeSensor(int ticketId, ArrayList<SensorMonitor> sensorMonitors) throws RuntimeException {//todo rivedere il test per le nuove funzionalità di attach, destach e per il lancio dell'ecczione se l'inserimento del nuovo sensore non va a buon fine
        try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
            Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId, maintainer.getId());
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
            map.clear();
            map.put("id", lastID);
            Sensor insertedSensor = sensorDAO.getSensors(map).getFirst();
            //FIXME: Non funziona perché i sensori sono istanze prese dal database, che non avranno alcun observer attaccato. Inoltre, io ne metterei solo uno
            for(SensorMonitor sm : sensorMonitors){//NOTA: i sensori iniziali sono attaccati nel Main
                changingSensor.detach(sm);
                insertedSensor.attach(sm);
            }
            closeTicket(ticketId);

        }catch (SQLException e){
            System.err.println("La sostituzione del sensore da parte identificato dal ticketId " + ticketId + " non è andata a buon fine");
            e.getStackTrace();
        }
    }


    public String repairSensor(double repairOrChange, int ticketId, ArrayList<SensorMonitor> sensorMonitors){//la probabilità viene passata come parametro altrimenti risulta troppo difficile da testare
        if(repairOrChange < 0.88) {

            try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()){
                Integer sensorId = ticketDAO.getSensorIdByTicket(ticketId, maintainer.getId());
                if (sensorId == null) throw new SQLException("Sensore non trovato");//fixme è possibile?

                sensorDAO.changeSensorState(sensorId, SensorState.ACTIVE);
                closeTicket(ticketId);
                System.out.println("il sensore è stato riparato");
                return "sensore_riparato";

            } catch (SQLException e) {
                System.err.println("Errore: transazione annullata per ticket " + ticketId);
                e.getStackTrace();
            }

        } else {
            try {
                changeSensor(ticketId, sensorMonitors);
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
