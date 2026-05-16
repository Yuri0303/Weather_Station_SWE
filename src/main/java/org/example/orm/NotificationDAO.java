package org.example.orm;

import org.example.domainmodel.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationDAO implements AutoCloseable {
    private Connection connection;

    public NotificationDAO(){
        try {
            this.connection = DatabaseManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public ArrayList<Notification> viewUnreadNotification(int userId) throws SQLException {
        String query = "SELECT * FROM NOTIFICATION WHERE userId = ? AND isRead = ?";
        ArrayList<Notification> notifications = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()){
                    notifications.add(new Notification(resultSet.getInt("id"), resultSet.getString("message"), resultSet.getObject("dateTime", LocalDateTime.class), resultSet.getBoolean("isRead"), resultSet.getInt("userId")));
                }
            } catch (SQLException e) {
                System.err.println("Errore durante la query delle notifiche non lette");
            }

        }catch (SQLException e){
            System.err.println("Errore durante il recupero delle notifiche non lette");
            e.getStackTrace();
        }

        return notifications;
    }

    public ArrayList<Notification> viewNotificationHistory(int userId, int lastDays) throws SQLException{
        String query = "SELECT * FROM NOTIFICATION WHERE userId = ? AND dateTime >= ?";
        ArrayList<Notification> notifications = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            LocalDateTime dateTime = LocalDateTime.now().minusDays(lastDays);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(dateTime));

            try(ResultSet resultSet = statement.executeQuery()) {
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

    public void setRead(int id) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement("UPDATE NOTIFICATION SET isRead = TRUE WHERE id = ?")){

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e){
            System.err.println("Errore durante il settaggio di <notifica letta> ");
            e.getStackTrace();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
