package org.example.orm;

import org.example.domainmodel.Measurement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class MeasurementDAO {
    private Connection connection;

    public MeasurementDAO() {
        try {
            connection = DatabaseManager.getInstance().getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public ArrayList<Measurement> getMeasurements(LocalDateTime startDate, LocalDateTime endDate, Map<String, Object> param) {
        StringBuilder query = new StringBuilder("SELECT * FROM MEASUREMENT");
        query.append(" WHERE ");
        if (param != null && !param.isEmpty()) {
            for (String key : param.keySet()) {
                query.append(key).append(" = ? AND ");
            }
            if (startDate == null && endDate == null)
                query.setLength(query.length() - 5);
        }

        if (startDate != null && endDate != null) {
            query.append("dateTime > ? AND dateTime < ?");
        } else if (startDate != null) {
            query.append("dateTime > ?");
        } else if (endDate != null) {
            query.append("dateTime < ?");
        }

        if ((param == null || param.isEmpty()) && startDate == null && endDate == null) {
            query.setLength(query.length() - 7); //remove WHERE
        }
        ArrayList<Measurement> measurements = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (param != null && !param.isEmpty()) {
                for (Object value : param.values()) {
                    statement.setObject(paramIndex, value);
                    paramIndex++;
                }
            }

            if (startDate != null && endDate != null) {
                statement.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(startDate));
                statement.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(endDate));
            } else if (startDate != null) {
                statement.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(startDate));
            } else if (endDate != null) {
                statement.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(endDate));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    measurements.add(new Measurement(resultSet.getInt("id"), resultSet.getInt("sensorId"), resultSet.getFloat("value"),
                            resultSet.getObject("dateTime", LocalDateTime.class)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during measurements retrieve: " + e.getMessage());
            e.getStackTrace();
        }
        return measurements;
    }

    public void addMeasurement(Measurement measurement) throws SQLException {   //FIXME: capire se usare valore di ritorno boolean oppure lancio eccezione
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO MEASUREMENT (value, dateTime, sensorId) VALUES (?, ?, ?)")) {
            statement.setFloat(1, measurement.getValue());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(measurement.getDateTime()));
            statement.setInt(3, measurement.getSensorId());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error during measurement insert: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }
}
