package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.AlertRuleDAO;
import org.example.orm.MeasurementDAO;
import org.example.orm.NotificationDAO;
import org.example.orm.SensorDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    public ArrayList<Measurement> readData(){
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()) {
            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            ArrayList<Measurement> lastMeasurements = new ArrayList<>();
            for (Sensor it : sensors){
                Integer lastMeasurementId = it.getLastMeasurementId();
                if (lastMeasurementId != null) {
                    map.put("id", lastMeasurementId);//Mappa che contiene tutti gli id delle ultime misurazioni di tutti i sensori attivi. Potrebbero essere nulle
                    lastMeasurements.add(measurementDAO.getMeasurements(map).getFirst());
                    map.clear();
                }
            }
            return lastMeasurements;
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura delle attuali misurazioni da parte dell'utente " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Measurement> readDataHistory(LocalDateTime startDate, LocalDateTime endDate){
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){
            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            ArrayList<Measurement> measurements = new ArrayList<>();
            for (Sensor it : sensors) {
                map.put("sensorId", it.getId());
                measurements.addAll(measurementDAO.getMeasurements(startDate, endDate, map));
                map.clear();
            }

            return measurements;
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura delle attuali misurazioni da parte dell'utente " + e.getMessage());
            return null;
        }
    }

    public void setAlertRule(int userId, SensorType sensorType, Float lowerBound, Float upperBound){
        try (AlertRuleDAO alertRuleDAO = new AlertRuleDAO()){
            alertRuleDAO.addAlertRule(userId, sensorType, lowerBound, upperBound);
        }catch (SQLException e){
            System.err.println("Errore durante l'inserimento di una nuova alert rule " + e.getMessage());
        }
    }

    public ArrayList<Notification> viewUnreadNotifications(int userId) {
        try (NotificationDAO notificationDAO = new NotificationDAO()){
            ArrayList<Notification> unreadNotifications = notificationDAO.viewUnreadNotifications(userId);
            for (Notification it : unreadNotifications)
                notificationDAO.setRead(it.getId());

            return unreadNotifications;
        }catch (SQLException e){
            System.err.println("Errore durante la visualizzazione delle notifiche non lette " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Notification> viewNotificationHistory(int userId, int lastDays){
        try (NotificationDAO notificationDAO = new NotificationDAO()){
            return notificationDAO.viewNotificationHistory(userId, lastDays);
        }catch (SQLException e){
            System.err.println("Errore durante la visualizzazione delle notifiche non lette " + e.getMessage());
            return null;
        }
    }

}
