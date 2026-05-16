package org.example.orm;

import org.example.domainmodel.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationDAO implements AutoCloseable{
    private Connection connection;

    public NotificationDAO(){
        try {
            this.connection = DatabaseManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws SQLException{
        if(connection != null)
            this.connection.close();
    }

    public ArrayList<Notification> viewUnreadNotification(int userId) throws SQLException{
        String query = "SELECT * FROM NOTIFICATION WHERE userId = ? AND isRead = ?";
        ArrayList<Notification> notifications = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            statement.setBoolean(2, false);

            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    notifications.add(new Notification(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getObject("dateTime", LocalDateTime.class), resultSet.getBoolean("isRead"), resultSet.getInt("userId")));
                }
            }catch (SQLException e){
                System.err.println("Errore durante la query delle notifiche non lette");
            }

        }catch (SQLException e){
            System.err.println("Errore durante il recupero delle notifiche non lette");
            e.getStackTrace();
        }

        return notifications;
    }

    public ArrayList<Notification> viewNotificationHistory(int userId, int lastDays) throws SQLException{
        String query = "SELECT * FROM NOTIFICATION WHERE userId = ? AND dateTime >= NOW() - (? * INTERVAL '1 day')";
        ArrayList<Notification> notifications = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            statement.setInt(2, lastDays);

            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    notifications.add(new Notification(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getTimestamp("dateTime").toLocalDateTime(), resultSet.getBoolean("isRead"), resultSet.getInt("userId")));
                }
            }catch (SQLException e){
                System.err.println("Errore durante la query dello storico notifiche");
            }
        }catch (SQLException e){
            System.err.println("Errore durante il recupero dello storico delle notifiche");
            e.getStackTrace();
        }

        return notifications;
    }

    public void setRead(ArrayList<Integer> ids) throws SQLException{
        StringBuilder query = new StringBuilder("UPDATE NOTIFICATION SET isRead = ?");

        if(ids != null && !ids.isEmpty()){
            query.append("WHERE");

            for(int i=0; i < ids.size(); i++){
                query.append(i).append(" userId = ? AND");
            }

            query.setLength(query.length() - 5);
        }
            try(PreparedStatement statement = connection.prepareStatement(query.toString())){

                statement.setBoolean(1, true);

                if(ids != null && !ids.isEmpty()) {
                    int paramIndex = 2;
                    for (Integer i : ids) {//ids poteva essere nullo, ho spostato la chiusura dell'if in fondo
                        statement.setInt(paramIndex++, i);
                    }
                }
                statement.executeUpdate();
            }catch (SQLException e){
                System.err.println("Errore durante il settaggio di <notifica letta> ");
                e.printStackTrace();
            }
        }


}
