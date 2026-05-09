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

    public ArrayList<Measurement> getMeasurements(Map<String, Object> param) {
        StringBuilder query = new StringBuilder("SELECT * FROM MEASUREMENT");
        if (param != null && !param.isEmpty()) {
            query.append(" WHERE ");
            for (String key : param.keySet()) {
                query.append(key).append(" = ? AND ");
            }
            query.setLength(query.length() - 5);
        }

        ArrayList<Measurement> measurements = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            if (param != null && !param.isEmpty()) {
                int paramIndex = 1;
                for (Object value : param.values()) {
                    statement.setObject(paramIndex, value);
                    paramIndex++;
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        measurements.add(new Measurement(resultSet.getInt("id"), resultSet.getInt("sensorId"), resultSet.getFloat("value"),
                                resultSet.getObject("dateTime", LocalDateTime.class)));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during measurements retrieve: " + e.getMessage());
            e.getStackTrace();
        }
        return measurements;
    }

    public void addMeasurement(Measurement measurement) throws SQLException {
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
