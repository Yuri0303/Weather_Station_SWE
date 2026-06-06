package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.AlertRuleDAO;
import org.example.orm.MeasurementDAO;
import org.example.orm.NotificationDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class SensorMonitor implements Observer {

    private ArrayList<AlertRule> verifyAlertRules(Measurement measurement, SensorType sensorType) {
        ArrayList<AlertRule> violatedAlertRules = new ArrayList<>();
        try (AlertRuleDAO alertRuleDAO = new AlertRuleDAO()) {
            ArrayList<AlertRule> alertRules = alertRuleDAO.getAlertRules(sensorType);
            for (AlertRule ar : alertRules) {
                boolean violated = ar.isViolatedBy(measurement);
                if (violated) {
                    violatedAlertRules.add(ar);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during verifyAlertRules: " + e.getMessage());
            e.getStackTrace();
        }
        return violatedAlertRules;
    }

    private ArrayList<Notification> createNotifications(ArrayList<AlertRule> ars, Measurement measurement, SensorType sensorType) {
        ArrayList<Notification> notifications = new ArrayList<>();
        for (AlertRule a : ars) {
            String message = "Attenzione: Violata regola su sensore di tipo " + sensorType.toString() + "/n/tValore registrato: " + measurement.getValue() + "/n/tLower bound: " + a.getLowerBound() + "/tUpper bound: " + a.getUpperBound();
            notifications.add(new Notification(0, message, LocalDateTime.now(), false, a.getUserId())); //id fittizio
        }
        return notifications;
    }

    @Override
    public void update(int measurementId, SensorType sensorType) {
        try (MeasurementDAO measurementDAO = new MeasurementDAO(); NotificationDAO notificationDAO = new NotificationDAO()) {
            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(Map.of("id", measurementId));
            Measurement m = measurements.getFirst();
            ArrayList<AlertRule> violatedAlertRules = verifyAlertRules(m, sensorType);
            ArrayList<Notification> notifications = createNotifications(violatedAlertRules, m, sensorType);
            for (Notification n : notifications) {
                notificationDAO.registerNotification(n);
            }

        }catch (SQLException e){
            System.err.println("Errore durante l'observer l'update di sensor monitor" + e.getMessage());
        }

    }
}
