package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.MeasurementDAO;
import org.example.orm.SensorDAO;
import org.example.orm.TicketDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class MaintainerControllerTest {

    Maintainer maintainer;
    MaintainerController maintainerController;
    AdminDatabaseController adminDatabaseController;
    @BeforeEach
    void setUp() {
        maintainer = new Maintainer(1, "Luke", "Skywalker", "lukeskywalker@test.it");
        maintainerController = new MaintainerController(maintainer);

        adminDatabaseController = new AdminDatabaseController();
        try{
            adminDatabaseController.resetDatabase();
            adminDatabaseController.createDatabase();
            adminDatabaseController.defaultInstances();
        }catch (SQLException e){
            System.out.println("Errore durante il reset del database");
        }
    }

    @Test
    void viewOpenTickets() {
        try (TicketDAO ticketDAO = new TicketDAO()){
            Ticket ticket1 = new Ticket(1, 1, null, true, false, null);
            Ticket ticket2 = new Ticket(2, 2, null, true, false, null);
            Ticket ticket4 = new Ticket(4, 4, null, true, false, null);//rimangono aperti l'1, il 2 e il 4

            ArrayList<Ticket> openTicketRest = new ArrayList<>();
            openTicketRest.add(ticket1);
            openTicketRest.add(ticket2);
            openTicketRest.add(ticket4);

            ticketDAO.addTicket(1);
            ticketDAO.addTicket(2);
            ticketDAO.addTicket(3);
            ticketDAO.addTicket(4);

            ticketDAO.closeTicket(3, maintainer.getId());

            ArrayList<Ticket> view = maintainerController.viewOpenTickets();

            assertIterableEquals(openTicketRest, view);
        }catch (SQLException e){
            System.err.println("Test viewOpenTicket exception");
        }
    }

    @Test
    void takeTicket_success() {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(1);
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        assertDoesNotThrow(() -> maintainerController.takeTicket(1));
    }

    @Test
    void takeTicket_notFound() {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(1);
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        assertThrows(SQLException.class ,() -> maintainerController.takeTicket(2));
    }

    @Test
    void takeTicket_alreadyTaken() {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(1);
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        try {
            maintainerController.takeTicket(1);
        } catch (SQLException e) {
           e.getStackTrace();
        }

        assertThrows(SQLException.class ,() -> maintainerController.takeTicket(1));
    }

    @Test
    void closeTicket_success() {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(1);
            maintainerController.takeTicket(1);
            assertDoesNotThrow(() -> maintainerController.closeTicket(1));
        }catch (SQLException e){
            System.err.println("Test viewOpenTicket exception");
        }
    }

    @Test
    void closeTicket_alreadyClosed() {
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.addTicket(1);
            maintainerController.takeTicket(1);
            maintainerController.closeTicket(1);
            assertThrows(SQLException.class, () -> maintainerController.takeTicket(1));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void closeTicket_notHisTicket() {
        try (TicketDAO ticketDAO = new TicketDAO()) {
            ticketDAO.addTicket(1);
            ticketDAO.takeTicket(1, 2);
            assertThrows(SQLException.class, () -> maintainerController.closeTicket(1));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void changeSensor_success() {
        try (TicketDAO ticketDAO = new TicketDAO(); SensorDAO sensorDAO = new SensorDAO()){

            Map<String, Object> param = new HashMap<>();
            sensorDAO.addSensor(SensorType.TEMPERATURE);
            ticketDAO.addTicket(13);
            ticketDAO.takeTicket(1, 1);

            maintainerController.changeSensor(1);

            //vediamo se le modifiche sono avvenute
            param.put("id", 13);
            assertEquals(SensorState.DEACTIVATED, sensorDAO.getSensors(param).getFirst().getSensorState());
            param.clear();
            param.put("id", 14);
            ArrayList<Sensor> sensors = sensorDAO.getSensors(param);
            Sensor t = sensors.getFirst();
            assertEquals(SensorState.ACTIVE, t.getSensorState());
            assertEquals(SensorType.TEMPERATURE, t.getSensorType());

        } catch (SQLException e) {
            System.err.println("Test viewOpenTicket exception");
        }
    }

    @Test
    void changeSensor_sensorAlreadyChanged() {
        try (TicketDAO ticketDAO = new TicketDAO(); SensorDAO sensorDAO = new SensorDAO()) {
            sensorDAO.addSensor(SensorType.TEMPERATURE);
            ticketDAO.addTicket(13);
            ticketDAO.takeTicket(1, 1);

            maintainerController.changeSensor(1);

            assertThrows(RuntimeException.class, () -> maintainerController.changeSensor(1));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void changeSensor_notHisTicket() {
        try (TicketDAO ticketDAO = new TicketDAO(); SensorDAO sensorDAO = new SensorDAO()) {
            sensorDAO.addSensor(SensorType.TEMPERATURE);
            ticketDAO.addTicket(13);
            ticketDAO.takeTicket(1, 2); //Un altro maintainer prende il ticket

            assertThrows(RuntimeException.class, () -> maintainerController.changeSensor(1));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void repairSensor() {
        double repairOrChange;
        boolean fixed = false, changed = false;
        try (SensorDAO sensorDAO = new SensorDAO(); TicketDAO ticketDAO = new TicketDAO()) {
            while (!fixed || !changed) {

                repairOrChange = Math.random();
                ticketDAO.addTicket(1);
                ticketDAO.takeTicket(1, 1);

                //forziamo il sensore a diventare guasto
                Map<String, Object> map = new HashMap<>();
                map.put("id", 1);
                ArrayList<Sensor> target = sensorDAO.getSensors(map);
                target.getFirst().sensorStateToFaulty();

                String result = maintainerController.repairSensor(repairOrChange, 1);

                if (repairOrChange < 0.88) {
                    target = sensorDAO.getSensors(map);
                    assertEquals(SensorState.ACTIVE, target.getFirst().getSensorState());
                    assertThrows(SQLException.class, () -> ticketDAO.closeTicket(1, maintainer.getId()));//fixme forse questa non è necessaria
                    assertEquals("sensore_riparato", result);
                    fixed = true;
                } else {
                    assertEquals("sensore_sostituito", result);//la sostituzione è già testata nella sua interezza
                    changed = true;
                }
                adminDatabaseController = new AdminDatabaseController();
                try{
                    adminDatabaseController.resetDatabase();
                    adminDatabaseController.createDatabase();
                    adminDatabaseController.defaultInstances();
                }catch (SQLException e){
                    System.out.println("Errore durante il reset del database");
                }
            }
        } catch (SQLException e) {
            System.err.println("Test repairSensor exception");
            e.printStackTrace();
        }

    }

    @AfterAll
    static void tearDownAll() {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
    }
}
