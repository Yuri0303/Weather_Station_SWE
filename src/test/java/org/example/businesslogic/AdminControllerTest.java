package org.example.businesslogic;

import org.example.domainmodel.Measurement;
import org.example.domainmodel.Ticket;
import org.example.domainmodel.User;
import org.example.orm.MeasurementDAO;
import org.example.orm.TicketDAO;
import org.example.orm.UserDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {

    AdminController  adminController;

    @BeforeEach
    void setUp() {
        adminController = new AdminController();

        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        try{
            adminDatabaseController.resetDatabase();
            adminDatabaseController.createDatabase();
            adminDatabaseController.defaultInstances();
        }catch (SQLException e){
            System.err.println("Errore durante la creazione delle istanze del database");
        }
    }

    @Test
    void readDataHistory() {
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 4, 0, 0);

        LocalDateTime date1 = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 1, 2, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 1, 4, 0, 0);
        LocalDateTime date4 = LocalDateTime.of(2026, 1, 5, 0, 0);

        Measurement m1 = new Measurement(0, 1, 10, date1);
        Measurement m2 = new Measurement(1, 1, 10, date2);
        Measurement m3 = new Measurement(2, 1, 10, date3);
        Measurement m4 = new Measurement(3, 1, 10, date4);

        try (MeasurementDAO measurementDAO = new MeasurementDAO()){
            measurementDAO.addMeasurement(m1);
            measurementDAO.addMeasurement(m2);
            measurementDAO.addMeasurement(m3);
            measurementDAO.addMeasurement(m4);
        }catch (SQLException e){
            System.err.println("Error in ReadDataHistory Test: " + e.getMessage());
        }

        ArrayList<Measurement> comparing = new ArrayList<>();
        comparing.add(m1);
        comparing.add(m2);
        comparing.add(m3);

        ArrayList<Measurement> results = adminController.readDataHistory(startDate, endDate);

        assertIterableEquals(results, comparing);
    }

    @Test
    void viewUsers() {
        //creare oggetti user non bloccati nel database iniziale
        User u1 = new User(2, "Roberto", "Chiesi", "robertochiesi@test.it", false);
        User u2 = new User(3, "Gianluca", "Taddei", "gianlucataddei@test.it", false);
        User u3 = new User(4, "Riccardo", "Cappellini", "riccardocappellini@test.it", false);

        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(u1);
        expectedUsers.add(u2);
        expectedUsers.add(u3);

        ArrayList<User> actualUsers = adminController.viewUsers();

        assertIterableEquals(expectedUsers, actualUsers);
    }

    @Test
    void blockUser() {
        ArrayList<User> users = new ArrayList<>();
        try (UserDAO userDAO = new UserDAO()) {
            users = userDAO.getUsers(Map.of("id", 2));
            assertEquals(1, users.size()); //Verifica che abbia prelevato un solo utente
        } catch (SQLException e) {
            System.err.println("Error in userDAO: " + e.getMessage());
        }
        User u1 = users.getFirst();
        adminController.blockUser(u1.getId());
        try (UserDAO userDAO = new UserDAO()) {
            users = userDAO.getUsers(Map.of("id", 2));
            assertEquals(1, users.size()); //Verifica che abbia prelevato un solo utente
        } catch (SQLException e) {
            System.err.println("Error in userDAO: " + e.getMessage());
        }
        User actualUser = users.getFirst();   //Adesso dovrebbe essere bloccato
        assertTrue(actualUser.isBlocked());

        //Provo a bloccare nuovamente lo stesso utente per vedere cosa succede
        adminController.blockUser(u1.getId());
        try (UserDAO userDAO = new UserDAO()) {
            users = userDAO.getUsers(Map.of("id", 2));
            assertEquals(1, users.size()); //Verifica che abbia prelevato un solo utente
        } catch (SQLException e) {
            System.err.println("Error in userDAO: " + e.getMessage());
        }
        actualUser = users.getFirst();   //Ora dovrebbe comunque essere bloccato
        assertTrue(actualUser.isBlocked());
    }

    @Test
    void openTicket() {
        Ticket expectedTicket = new Ticket(1, 2, null, true, false, null);
        Ticket actualTicket;
        try (TicketDAO ticketDAO = new TicketDAO()) {
            adminController.openTicket(2);
            actualTicket = ticketDAO.getOpenTickets().getFirst();
            System.out.println(expectedTicket);
            System.out.println(actualTicket);
            assertEquals(expectedTicket, actualTicket);
            assertThrows(SQLException.class, () -> adminController.openTicket(2)); //non può inserire un altro ticket se ce ne è già uno aperto
        } catch (SQLException e) {
            System.err.println("Error in ticketDAO: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
    }
}