package org.example.businesslogic;

import org.example.domainmodel.Measurement;
import org.example.domainmodel.User;
import org.example.orm.MeasurementDAO;
import org.example.orm.TicketDAO;
import org.example.orm.UserDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class AdminController {
    public ArrayList<Measurement> readDataHistory(LocalDateTime startDate, LocalDateTime endDate) {
        try (MeasurementDAO measurementDAO = new MeasurementDAO()){
            return measurementDAO.getMeasurements(startDate, endDate, null);
        }catch (SQLException e){
            System.err.println("Errore durante la lettura dello storico dei dati" + e.getMessage());
            return null;
        }
    }

    public ArrayList<Measurement> readDataHistory(LocalDateTime startDate, LocalDateTime endDate, int sensorId) {
        try (MeasurementDAO measurementDAO = new MeasurementDAO()) {
            return measurementDAO.getMeasurements(startDate, endDate, Map.of("sensorId", sensorId));
        }catch (SQLException e){
            System.err.println("Errore durante la lettura dello storico dati" + e.getMessage());
            return  null;
        }
    }

    public ArrayList<User> viewUsers() {

        try (UserDAO userDAO = new UserDAO();) {
            return userDAO.getUsers(Map.of("isBlocked", false));
        }catch (SQLException e){
            System.err.println("Errore durante la lettura degli utenti" + e.getMessage());
            return  null;
        }
    }

    public void blockUser(int userId) {
        try (UserDAO userDAO = new UserDAO()) {
            userDAO.blockUser(userId);
        }catch (SQLException e){
            System.err.println("Errore durante il bloccaggio di un utente" + e.getMessage());
        }
    }

    public void openTicket(int sensorId) {
        try (TicketDAO ticketDAO = new TicketDAO()){
            ticketDAO.addTicket(sensorId);
        } catch (SQLException e) {
            System.err.println("Error during ticket opening: " + e.getMessage());
        }
    }
}
