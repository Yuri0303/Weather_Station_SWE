package org.example.orm;

import org.example.domainmodel.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class SensorDAO implements AutoCloseable {
    private Connection connection;

    public SensorDAO() {
        try {
            this.connection = DatabaseManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public ArrayList<Sensor> getSensorsByState(SensorState sensorState) throws SQLException {
        String query = "SELECT * FROM \"Sensor\" WHERE sensorState = ?";
        ArrayList<Sensor> sensors = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, sensorState.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    //Integer lastMeas = resultSet.getObject("lastMeasurementId"); todo fixare
                    int lastMeas = resultSet.getInt("lastMeasurementId");
                    SensorType myType = SensorType.valueOf(resultSet.getString("sensorType"));
                    SensorState myState = SensorState.valueOf(resultSet.getString("sensorState"));

                    Sensor sensor = switch (myType) {
                        case HUMIDITY -> new HumiditySensor(id, lastMeas, myType, myState);
                        case WIND -> new WindSensor(id, lastMeas, myType, myState);
                        case TEMPERATURE -> new TemperatureSensor(id, lastMeas, myType, myState);
                        case PRESSURE -> new PressureSensor(id, lastMeas, myType, myState);
                    };

                    sensors.add(sensor);
                }
            } catch (SQLException e) {
                System.err.println("Errore durante la query del recupero dei sensori guasti: " + e.getMessage());
                e.getStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dei sensori guasti: " + e.getMessage());
            e.getStackTrace();
        }
        return sensors;
    }

    public ArrayList<Sensor> getSensors(Map<String, Object> param) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM \"Sensor\"");

        if(param != null && !param.isEmpty()){
            query.append(" WHERE ");
            for (String key : param.keySet())
                query.append(key).append(" = ? AND ");

            query.setLength(query.length() - 5);
        }

        ArrayList<Sensor> sensors = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query.toString())){

            if(param != null && !param.isEmpty()) {
                int paramIndex = 1;
                for (Object value : param.values())
                    statement.setObject(paramIndex++, value);
            }

            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int lastMeas = resultSet.getInt("lastMeasurementId");
                    SensorType myType = SensorType.valueOf(resultSet.getString("sensorType").toUpperCase());
                    SensorState state = SensorState.valueOf(resultSet.getString("sensorState").toUpperCase());

                    Sensor sensor = switch (myType) {
                        case HUMIDITY -> new HumiditySensor(id, lastMeas, myType, state);
                        case WIND -> new WindSensor(id, lastMeas, myType, state);
                        case TEMPERATURE -> new TemperatureSensor(id, lastMeas, myType, state);
                        case PRESSURE -> new PressureSensor(id, lastMeas, myType, state);
                    };

                    sensors.add(sensor);
                }
            }catch (SQLException e){
                System.err.println("Errore durante la query del recupero di sensori: "+e.getMessage());
                e.getStackTrace();
            }
        }catch (SQLException e){
            System.err.println("Errore durante il recupero di sensori: "+ e.getMessage());
            e.getStackTrace();
        }

        return sensors;
    }


    public void changeSensorState(int sensorId, SensorState newState) throws SQLException{
        String query = "UPDATE \"Sensor\" SET sensorState = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, newState.name());
            statement.setInt(2, sensorId);

            statement.executeUpdate();
        }catch (SQLException e){
            System.err.println("Errore durante il settaggio dello stato del sensore: "+e.getMessage());
            e.getStackTrace();
        }
    }

    public void addSensor(SensorType sensorType) throws SQLException{
        String query = "INSERT INTO \"Sensor\" (lastMeasurementId, sensorType, sensorState) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setNull(1, Types.INTEGER);
            statement.setString(2, sensorType.name());
            statement.setString(3, SensorState.ACTIVE.name());

            statement.executeUpdate();
        }catch (SQLException e){
            System.err.println("Errore durante l'inserimento di un nuovo sensore: " + e.getMessage());
            e.getStackTrace();
        }
    }

    public void updateLastMeasurement(int sensorId, int newLastMeasurementId) throws RuntimeException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE \"Sensor\" SET lastMeasurementId = ? WHERE id = ?")) {
            statement.setInt(1, newLastMeasurementId);
            statement.setInt(2, sensorId);
            int updateCount = statement.executeUpdate();
            if (updateCount != 1) {
                throw new RuntimeException("Error in updating last measurement: updateCount = " + updateCount);
            }
        } catch (SQLException e) {
            System.err.println("Error in updating last measurement: " + e.getMessage());
            e.getStackTrace();
        }
    }

    @Override
    public void close() throws SQLException{
        if(connection != null)
            this.connection.close();
    }
}
