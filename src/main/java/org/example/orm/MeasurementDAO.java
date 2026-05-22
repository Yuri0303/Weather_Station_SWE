package org.example.orm;

import org.example.domainmodel.Measurement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class MeasurementDAO implements AutoCloseable{
    private Connection connection;

    public MeasurementDAO() {
        try {
            connection = DatabaseManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws SQLException{
        if(connection != null)
            this.connection.close();
    }

    public ArrayList<Measurement> getMeasurements(Map<String, Object> param) {
        StringBuilder query = new StringBuilder("SELECT * FROM \"Measurement\"");
        query.append(" WHERE ");
        if (param != null && !param.isEmpty()) {
            for (String key : param.keySet()) {
                query.append(key).append(" = ? AND ");
            }

                query.setLength(query.length() - 5);
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

    public ArrayList<Measurement> getMeasurements(LocalDateTime startDate, LocalDateTime endDate, Map<String, Object> param) {
        StringBuilder query = new StringBuilder("SELECT * FROM \"Measurement\"");
        query.append(" WHERE dateTime >= ? AND dateTime <= ?");
        if (param != null && !param.isEmpty()) {
            query.append(" AND ");
            for (String key : param.keySet()) {
                query.append(key).append(" = ? AND ");
            }
            query.setLength(query.length() - 5);
        }

        ArrayList<Measurement> measurements = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(startDate));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(endDate));
            int paramIndex = 3;
            if (param != null && !param.isEmpty()) {
                for (Object value : param.values()) {
                    statement.setObject(paramIndex, value);
                    paramIndex++;
                }
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

    public int addMeasurement(Measurement measurement) throws SQLException { //FIXME: capire se usare valore di ritorno boolean oppure lancio eccezione
        int generatedId;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO \"Measurement\" (value, dateTime, sensorId) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setFloat(1, measurement.getValue());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(measurement.getDateTime()));
            statement.setInt(3, measurement.getSensorId());

            statement.executeUpdate();

            try(ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                } else {
                    throw new SQLException("Error in adding measurement: no PK generated");
                }
            }
            return generatedId;

        } catch (SQLException e) {
            System.err.println("Error during measurement insert: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }


}
