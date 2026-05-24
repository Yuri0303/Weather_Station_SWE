package org.example.businesslogic;

import org.example.domainmodel.Admin;
import org.example.domainmodel.Maintainer;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class StaffLoginControllerTest {

    private StaffLoginController staffLoginController;
    @BeforeEach
    void setUp() {
        staffLoginController = new StaffLoginController();
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
        adminDatabaseController.createDatabase();
        try {
            adminDatabaseController.defaultInstances();
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione delle istanze del database");
        }
    }

    @DisplayName("Admin presente e credenziali esatte")
    @Test
    void adminLogin_success() {
        Admin admin = new Admin(1, "Giulio", "Nencini", "giulionencini@test.it");
        assertEquals(admin, staffLoginController.adminLogin("giulionencini@test.it", "123"));
    }

    @DisplayName("Admin presente, ma credenziali errate")
    @Test
    void adminLogin_fail1() {
        assertNull(staffLoginController.adminLogin("giulionencini@test.it", "abc"));
        assertNull(staffLoginController.adminLogin("giulionencini@email.it", "123"));
    }

    @DisplayName("Admin non presente nel database")
    @Test
    void adminLogin_fail2() {
        assertNull(staffLoginController.adminLogin("samuelezanieri@test.it", "123"));
    }

    @DisplayName("Manutentore presente e credenziali giuste")
    @Test
    void maintainerLogin_success() {
        Maintainer maintainer = new Maintainer(1, "Damiano", "Nencini", "damianonencini@test.it");
        assertEquals(maintainer, staffLoginController.maintainerLogin("damianonencini@test.it", "123"));
    }

    @DisplayName("Manutentore presente, ma credenziali sbagliate")
    @Test
    void maintainerLogin_fail1() {
        assertNull(staffLoginController.maintainerLogin("damianonencini@test.it", "abc"));
        assertNull(staffLoginController.maintainerLogin("damianonencini@email.it", "123"));
    }

    @DisplayName("Manutentore non presente nel database")
    @Test
    void maintainerLogin_fail2() {
        assertNull(staffLoginController.maintainerLogin("samuelezanieri@test.it", "123"));
    }

    @AfterAll
    static void tearDownAll() {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
    }
}