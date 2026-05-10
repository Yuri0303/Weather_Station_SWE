package org.example.orm;

import org.example.domainmodel.AlertRule;
import org.example.domainmodel.SensorType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class AlertRuleDAO {
    private Connection connection;

    public AlertRuleDAO(){
        try {
            this.connection = DatabaseManager.getInstance().getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void addAlertRule(SensorType sensorType, Float lowerBound, Float upperBound, int userId) throws SQLException{
        String query = "INSERT INTO ALERTRULE (sensorType, lowerBound, upperBound, userId) VALUES (?,?,?,?)";

        try(PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, sensorType.name());
            statement.setInt(4, userId);

            if(lowerBound != null){
                statement.setNull(2, Types.FLOAT);
            }
            else
                statement.setFloat(2, lowerBound);

            if(upperBound != null){
                statement.setNull(3, Types.FLOAT);
            }
            else
                statement.setFloat(3, upperBound);


            statement.executeUpdate();
        }catch (SQLException e){
            System.err.println("Errore nell'inserimento di una nuova alert rule");
            e.getStackTrace();
        }
    }

    //fixme funzione alternativa
    public ArrayList<AlertRule> getAlertRules(Map<String, Object> param) throws SQLException{
        StringBuilder query = new StringBuilder("SELECT * FROM ALERTRULE");

        if(param != null && !param.isEmpty()){
            query.append("WHERE");
            for (String key : param.keySet())
                query.append(key).append(" = ? AND ");
            query.setLength(query.length() - 5);
        }

        ArrayList<AlertRule> alertRules = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query.toString())){
            if(param != null && !param.isEmpty()){
                int paramIndex = 1;
                for (Object value : param.values())
                    statement.setObject(paramIndex++, value);

                try (ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()){
                        alertRules.add(new AlertRule(resultSet.getInt("id"), resultSet.getInt("userId"), resultSet.getFloat("lowerBound"),
                                resultSet.getFloat("upperBound"), SensorType.valueOf(resultSet.getString("sensorType"))));
                    }
                }
            }
        }catch (SQLException e){
            System.err.println("Errore durante l'estrazione delle alert rules");
            e.getStackTrace();
        }

        return alertRules;
    }

    public ArrayList<AlertRule> getAlertRules(SensorType sensorType) throws SQLException{
        String query = "SELECT * FROM ALERTRULE WHERE sensorType = ?";
        ArrayList<AlertRule> alertRules = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, sensorType.name());

            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next())
                    alertRules.add(new AlertRule(resultSet.getInt("id"), resultSet.getInt("userId"), resultSet.getFloat("lowerBound"),
                            resultSet.getFloat("upperBound"), SensorType.valueOf(resultSet.getString("sensorType"))));
            }
        }catch (SQLException e){
            System.err.println("Errore durante l'estrazione delle alter rules");
            e.getStackTrace();
        }

        return alertRules;
    }

}
