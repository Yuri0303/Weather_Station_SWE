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
        MeasurementDAO measurementDAO = new MeasurementDAO();
        return measurementDAO.getMeasurements(startDate, endDate, null);
    }

    public ArrayList<Measurement> readDataHistory(LocalDateTime startDate, LocalDateTime endDate, int sensorId) {
        MeasurementDAO measurementDAO = new MeasurementDAO();
        return measurementDAO.getMeasurements(startDate, endDate, Map.of("sensorId", sensorId));
    }

    public ArrayList<User> viewUsers() {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUsers(Map.of("isBlocked", false));
    }

    public void blockUser(int userId) {
        UserDAO userDAO = new UserDAO();
        userDAO.blockUser(userId);
    }

    public void openTicket(int sensorId) {
        TicketDAO ticketDAO = new TicketDAO();
        try {
            ticketDAO.addTicket(sensorId);
        } catch (SQLException e) {
            System.err.println("Error during ticket opening: " + e.getMessage());
        }
    }
}
