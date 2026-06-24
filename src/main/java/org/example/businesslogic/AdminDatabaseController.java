package org.example.businesslogic;

import org.example.orm.AdminDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class AdminDatabaseController {
    public void resetDatabase() {
        StringBuilder sql_tmp = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/reset.sql"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) { sql_tmp.append(line).append("\n"); }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file \"reset.sql\": " + e.getMessage());
            return;
        }
        String sql = sql_tmp.toString();
        try (AdminDAO adminDAO = new AdminDAO()) {
            adminDAO.resetDatabase(sql);
        } catch (SQLException e) {
            System.err.println("Error in resetDatabase: " + e.getMessage());
            e.getStackTrace();
        }
    }

    public void createDatabase() {
        StringBuilder sql_tmp = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/schema.sql"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) { sql_tmp.append(line).append("\n"); }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file \"schema.sql\": " + e.getMessage());
            return;
        }
        String sql = sql_tmp.toString();
        try (AdminDAO adminDAO = new AdminDAO()) {
            adminDAO.createDatabase(sql);
        } catch (SQLException e) {
            System.err.println("Error in createDatabase: " + e.getMessage());
            e.getStackTrace();
        }
    }

    public void defaultInstances() throws SQLException {
        StringBuilder sql_tmp = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/initDatabase.sql"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) { sql_tmp.append(line).append("\n"); }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file \"default.sql\": " + e.getMessage());
            return;
        }
        String sql = sql_tmp.toString();
        try (AdminDAO adminDAO = new AdminDAO()) {
            adminDAO.generateDefaultInstances(sql);
        }  catch (SQLException e) {
            System.err.println("Error in defaultInstances: " + e.getMessage());
        }
    }
}
