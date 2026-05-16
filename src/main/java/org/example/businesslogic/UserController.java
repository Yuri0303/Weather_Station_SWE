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
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){
            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            for (Sensor it : sensors){
                Integer idLastMeasurement = it.getIdLastMeasurement();
                if(idLastMeasurement != null)
                    map.put("id", idLastMeasurement);//mappa che contiene tutti gli id delle ultime misurazioni di tutti i sensori attivi. Potrebbero essere nulle
            }

            return measurementDAO.getMeasurements(map);
        }catch (SQLException e){
            System.err.println("Errore durante la lettura delle attauli misurazioni da parte dell'utente " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Measurement> readDataHistory(LocalDateTime starDate, LocalDateTime endDate){
        try (SensorDAO sensorDAO = new SensorDAO(); MeasurementDAO measurementDAO = new MeasurementDAO()){
            ArrayList<Sensor> sensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
            Map<String, Object> map = new HashMap<>();
            for (Sensor it : sensors){
                Integer idLastMeasurement = it.getIdLastMeasurement();
                if(idLastMeasurement != null)
                    map.put("id", idLastMeasurement);//mappa che contiene tutti gli id delle ultime misurazioni di tutti i sensori attivi. Potrebbero essere nulle
            }

            return measurementDAO.getMeasurements(starDate, endDate, map);//fixme fa esattamente la stessa cosa di readData(), solo che chiama l'altra funzione
        }catch (SQLException e){
            System.err.println("Errore durante la lettura delle attauli misurazioni da parte dell'utente " + e.getMessage());
            return null;
        }
    }

    public void setAlertRule(int userId, SensorType sensorType, float lowerBound, float upperBound){
        try (AlertRuleDAO alertRuleDAO = new AlertRuleDAO()){
            alertRuleDAO.addAlertRule(userId, sensorType, lowerBound, upperBound);
        }catch (SQLException e){
            System.err.println("Errore nell'aggiunzione di una nuova alert rule " + e.getMessage());
        }
    }

    public ArrayList<Notification> viewUnreadNotifications(int userId){
        try (NotificationDAO notificationDAO = new NotificationDAO()){
            ArrayList<Notification> unreadNotifications = notificationDAO.viewUnreadNotification(userId);
            for (Notification it : unreadNotifications)//fixme capire se fare così o in altro modo
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
