package org.example.businesslogic;

import org.example.domainmodel.*;
import org.example.orm.AlertRuleDAO;
import org.example.orm.MeasurementDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SensorMonitor implements Observer {

    public ArrayList<AlertRule> verifyAlertRules(Measurement measurement, SensorType sensorType) {
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

    public ArrayList<Notification> createNotifications(ArrayList<AlertRule> ars, Measurement measurement, SensorType sensorType) {
        //TODO: create notifications and return
    }

    @Override
    public void update(int measurementId, SensorType sensorType) {
        try (MeasurementDAO measurementDAO = new MeasurementDAO()) {
            ArrayList<Measurement> measurements = measurementDAO.getMeasurements(null, null, Map.of("id", measurementId));
            Measurement m = measurements.getFirst();
            ArrayList<AlertRule> violatedAlertRules = verifyAlertRules(m, sensorType);

        }catch (SQLException e){
            System.err.println("Errore durante l'observer l'update di sensor monitor" + e.getMessage());
        }
        //TODO: create notifications and save with NotificationDAO
    }
}
