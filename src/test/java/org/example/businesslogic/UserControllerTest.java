package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.AlertRuleDAO;
import org.example.orm.MeasurementDAO;
import org.example.orm.NotificationDAO;
import org.example.orm.SensorDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController;
    @BeforeEach
    void setUp() {
        userController = new UserController();

        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        try {
            adminDatabaseController.resetDatabase();
            adminDatabaseController.createDatabase();
            adminDatabaseController.defaultInstances();
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione delle istanze del database");
        }
    }


    @Test
    void readData() {
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){
            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            ArrayList<Measurement> measurements = new ArrayList<>();
            int idM = 5;
            for (Sensor s : sensors){
                Measurement m = new Measurement(idM, s.getId(), s.measure(), LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
                int idLastMeasurement = measurementDAO.addMeasurement(m);
                sensorDAO.updateLastMeasurement(s.getId(), idLastMeasurement);
                measurements.add(m);
            }

            ArrayList<Measurement> results = userController.readData();
            assertIterableEquals(results, measurements);

            //verifichiamo che prenda effettivamente le ultime misure effettuate
            measurements.clear();
            for (Sensor s : sensors){
                Measurement m = new Measurement(idM, s.getId(), s.measure(), LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
                int idLastMeasurement = measurementDAO.addMeasurement(m);
                sensorDAO.updateLastMeasurement(s.getId(), idLastMeasurement);
                measurements.add(m);
            }

            results = userController.readData();
            assertIterableEquals(results, measurements);
        }catch (SQLException e){
            System.err.println("Error readData Test: "+e.getMessage());
            e.getStackTrace();
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

        Measurement m1 = new Measurement(5, 1, 10, date1);
        Measurement m2 = new Measurement(6, 1, 10, date2);
        Measurement m3 = new Measurement(7, 1, 10, date3);
        Measurement m4 = new Measurement(8, 1, 10, date4);

        try (MeasurementDAO measurementDAO = new MeasurementDAO()){
            measurementDAO.addMeasurement(m1);
            measurementDAO.addMeasurement(m2);
            measurementDAO.addMeasurement(m3);
            measurementDAO.addMeasurement(m4);
        }catch (SQLException e){
            System.err.println("Error readDataHistory Test: " + e.getMessage());
        }

        ArrayList<Measurement> comparing = new ArrayList<>();
        comparing.add(m1);
        comparing.add(m2);
        comparing.add(m3);

        ArrayList<Measurement> results = userController.readDataHistory(startDate, endDate);

        assertIterableEquals(results, comparing);
    }

    @Test
    void setAlertRule() {
        userController.setAlertRule(1, SensorType.TEMPERATURE, 11f, 51f);
        userController.setAlertRule(2, SensorType.WIND, null, 89f);
        userController.setAlertRule(3, SensorType.HUMIDITY, 36f, null);

        ArrayList<AlertRule> comparing = new ArrayList<>();
        ArrayList<AlertRule> results = new ArrayList<>();
        comparing.add(new AlertRule(7, 1, 11f, 51f, SensorType.TEMPERATURE));
        comparing.add(new AlertRule(8, 2, null, 89f, SensorType.WIND));
        comparing.add(new AlertRule(9, 3, 36f, null, SensorType.HUMIDITY));
        try (AlertRuleDAO alertRuleDAO = new AlertRuleDAO()){
            Map<String, Object> map = new HashMap<>();
            map.put("id", 7);
            results.add(alertRuleDAO.getAlertRules(map).getFirst());
            map.clear();
            map.put("id", 8);
            results.add(alertRuleDAO.getAlertRules(map).getFirst());
            map.clear();
            map.put("id", 9);
            results.add(alertRuleDAO.getAlertRules(map).getFirst());

        }catch (SQLException e){
            System.err.println("Error in setAlertRule Test: "+e.getMessage());
        }

        assertIterableEquals(comparing, results);
    }

    @Test
    void viewUnreadNotifications() {//che siano quelle e che dopo siano state settate a read
        try (NotificationDAO notificationDAO = new NotificationDAO()){
            LocalDateTime d1 = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime d2 = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

            notificationDAO.registerNotification(new Notification(0, "testo1", d1, false, 1));
            notificationDAO.registerNotification(new Notification(0, "testo2", d2, false, 1));
            ArrayList<Notification> comparing = new ArrayList<>();
            comparing.add(new Notification(3, "testo1", d1, false, 1));
            comparing.add(new Notification(4, "testo2", d2, false, 1));

            ArrayList<Notification> notifications = userController.viewUnreadNotifications(1);

            assertIterableEquals(comparing, notifications);
            assertTrue(userController.viewUnreadNotifications(1).isEmpty());
        }catch (SQLException e){
            System.err.println("Error in viewUnreadNotifications Test: "+e.getMessage());
        }
    }

    @Test
    void viewNotificationHistory() {
        try (NotificationDAO notificationDAO = new NotificationDAO()){
            LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

            LocalDateTime d1 = today.minusDays(1);
            LocalDateTime d2 = today.minusDays(2);
            LocalDateTime d3 = today.minusDays(3);
            LocalDateTime d4 = today.minusDays(4);
            notificationDAO.registerNotification(new Notification(0, "testo1", d1, false, 1));
            notificationDAO.registerNotification(new Notification(0, "testo2", d2, false, 1));
            notificationDAO.registerNotification(new Notification(0, "testo3", d3, false, 1));
            notificationDAO.registerNotification(new Notification(0, "testo4", d4, false, 1));

            ArrayList<Notification> comparing = new ArrayList<>();
            comparing.add(new Notification(3, "testo1", d1, false, 1));
            comparing.add(new Notification(4, "testo2", d2, false, 1));
            comparing.add(new Notification(5, "testo3", d3, false, 1));

            ArrayList<Notification> results = userController.viewNotificationHistory(1, 3);

            for (Notification n : comparing)
                System.out.println(n.toString());
            for (Notification n : results)
                System.out.println(n.toString());
            assertIterableEquals(comparing, results);
        }catch (SQLException e){
            System.err.println("Error in viewUnreadNotifications Test: "+e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
    }
}