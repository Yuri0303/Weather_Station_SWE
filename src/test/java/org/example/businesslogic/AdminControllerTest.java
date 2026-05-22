package org.example.businesslogic;

import org.example.domainmodel.Admin;
import org.example.domainmodel.Measurement;
import org.example.orm.MeasurementDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {

    Admin admin;
    AdminController  adminController;

    @BeforeEach
    void setUp() {
        StaffLoginController loginAdminController = new StaffLoginController();
        //admin = loginAdminController.adminLogin("giulionenini@test.it", "123");
        adminController = new AdminController();

        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        try{
            adminDatabaseController.resetDatabase();
            adminDatabaseController.createDatabase();
            adminDatabaseController.defaultInstances();
        }catch (SQLException e){
            System.out.println("Errore durante il reset del database");
        }
    }

    @Test
    void readDataHistory() {
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 4, 0, 0);

        LocalDateTime date1 = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2026, 1, 2, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 1, 4, 0, 0);
        LocalDateTime date4 = LocalDateTime.of(2026, 1, 5, 0, 0);

        Measurement m1 = new Measurement(0, 1, 10, date1);
        Measurement m2 = new Measurement(1, 1, 10, date2);
        Measurement m3 = new Measurement(2, 1, 10, date3);
        Measurement m4 = new Measurement(3, 1, 10, date4);

        try (MeasurementDAO measurementDAO = new MeasurementDAO()){
            measurementDAO.addMeasurement(m1);
            measurementDAO.addMeasurement(m2);
            measurementDAO.addMeasurement(m3);
            measurementDAO.addMeasurement(m4);
        }catch (SQLException e){

        }

        ArrayList<Measurement> comparing = new ArrayList<>();
        comparing.add(m1);
        comparing.add(m2);
        comparing.add(m3);

        ArrayList<Measurement> results = adminController.readDataHistory(startDate, endDate);
        System.out.println(results.toString());

        assertIterableEquals(results, comparing);
    }

    @Test
    void viewUsers() {
    }

    @Test
    void blockUser() {
    }

    @Test
    void openTicket() {
    }
}