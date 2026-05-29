package org.example.orm;

import org.example.domainmodel.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TicketDAO implements AutoCloseable{
    private Connection connection;

    public TicketDAO() {
        try {
            connection = DatabaseManager.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public void addTicket(int sensorId) throws SQLException {   //FIXME: capire se usare valore di ritorno boolean oppure lancio eccezione

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO \"Ticket\" (isOpen, closeDateTime, isTaken, maintainerId, sensorId) VALUES (?, ?, ?, ?, ?)")) {
            statement.setBoolean(1, true);
            statement.setNull(2, Types.TIMESTAMP);
            statement.setBoolean(3, false);
            statement.setNull(4, Types.INTEGER);
            statement.setInt(5, sensorId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public ArrayList<Ticket> getOpenTickets(){
        ArrayList<Ticket> tickets = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Ticket\" WHERE isOpen = ?")) {
            statement.setBoolean(1, true);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp closeDateTime = resultSet.getTimestamp("closeDateTime");
                    LocalDateTime closeDateTimeLocal = closeDateTime != null ? closeDateTime.toLocalDateTime() : null;
                    tickets.add(new Ticket(resultSet.getInt("id"), resultSet.getInt("sensorId"), resultSet.getObject("maintainerId", Integer.class),
                            resultSet.getBoolean("isOpen"), resultSet.getBoolean("isTaken"), closeDateTimeLocal));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in retrieving open tickets: " + e.getMessage());
            e.getStackTrace();
        }

        return tickets;
    }

    public void takeTicket(int ticketId, int maintainerId) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement("UPDATE \"Ticket\" SET maintainerId = ?, isTaken = ? WHERE id = ? AND maintainerId IS NULL AND isTaken = false")) {
            statement.setInt(1, maintainerId);
            statement.setBoolean(2, true);
            statement.setInt(3, ticketId);
            int updateCount = statement.executeUpdate();
            if (updateCount != 1)
                throw new SQLException("Ticket not found or update failed. Rows affected: " + updateCount);
        } catch (SQLException e) {
            System.err.println("Error while taking ticket: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }

    public void closeTicket(int ticketId, int maintainerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE \"Ticket\" SET isOpen = false, closeDateTime = ? WHERE id = ? AND maintainerid = ? AND isOpen = true")) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, ticketId);
            statement.setInt(3, maintainerId);

            int updateCount = statement.executeUpdate();
            if (updateCount != 1)
                throw new SQLException("Ticket not found or update failed. Rows affected: " + updateCount);
        } catch (SQLException e) {
            System.out.println("Error in closing ticket: " + e.getMessage());
            e.getStackTrace();
            throw e;
        }
    }

    public Integer getSensorIdByTicket(int ticketId, int maintainerId) throws SQLException { //NOTA: è necessario che questa si propaghi, perché successivamente non può essere accettato un valore null
        String query = "SELECT * FROM \"Ticket\" WHERE id = ? AND maintainerid = ? AND isOpen = true";
        Integer result = null;

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, ticketId);
            statement.setInt(2, maintainerId);

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    Timestamp closeDateTime = resultSet.getTimestamp("closeDateTime");
                    LocalDateTime closeDateTimeLocal = closeDateTime != null ? closeDateTime.toLocalDateTime() : null;
                    Ticket ticket = new Ticket(resultSet.getInt("id"), resultSet.getInt("sensorId"), resultSet.getInt("maintainerId"),
                            resultSet.getBoolean("isOpen"), resultSet.getBoolean("isTaken"), closeDateTimeLocal);
                    result = ticket.getSensorId();
                }
            }
        }catch (SQLException e){
            System.err.println("Errore: recupero id del sensore di un ticket non riuscito");
            e.getStackTrace();
        }

        return result;
    }

    @Override
    public void close() throws SQLException{
        if(connection != null)
            this.connection.close();
    }
}
