package org.example.businesslogic;

import org.example.domainmodel.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginControllerTest {

    UserLoginController userLoginController;

    @BeforeEach
    void setUp() {
        userLoginController = new UserLoginController();
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
        adminDatabaseController.createDatabase();
        try {
            adminDatabaseController.defaultInstances();
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione delle istanze del database");
        }
    }

    @DisplayName("User presente e credenziali esatte")
    @Test
    void login_success() {
        User user = new User(2, "Roberto", "Chiesi", "robertochiesi@test.it", false);
        assertEquals(user, userLoginController.login("robertochiesi@test.it", "123"));
    }

    @DisplayName("User presente, ma credenziali errate")
    @Test
    void login_fail1() {
        assertNull(userLoginController.login("robertochiesi@test.it", "abc"));
        assertNull(userLoginController.login("robertochiesi@email.it", "123"));
    }

    @DisplayName("User non presente nel database")
    @Test
    void login_fail2() {
        assertNull(userLoginController.login("yuribartoletti@test.it", "123"));
    }

    @DisplayName("User presente, ma bloccato")
    @Test
    void login_fail3() {
        assertNull(userLoginController.login("samuelezanieri@test.it", "123"));
    }

    @DisplayName("Registrazione con successo")
    @Test
    void register_success() {
        assertTrue(userLoginController.register("Luke", "Skywalker", "lukeskywalker@test.it", "123"));
        assertNotNull(userLoginController.login("lukeskywalker@test.it", "123"));
    }

    @DisplayName("User già registrato")
    @Test
    void register_fail1() {
        assertFalse(userLoginController.register("Samuele", "Skywalker", "samuelezanieri@test.it", "123"));
    }

    @DisplayName("Email o password troppo lunghe")
    @Test
    void register_fail2() {
        StringBuilder stringBuilder = new StringBuilder(55);
        stringBuilder.repeat("a", Math.max(0, stringBuilder.capacity()));
        String email = stringBuilder.toString();
        assertFalse(userLoginController.register("Luke","Skywalker", email, "123"));
        stringBuilder = new StringBuilder(260);
        stringBuilder.repeat("a", Math.max(0, stringBuilder.capacity()));
        String password = stringBuilder.toString();
        assertFalse(userLoginController.register("Luke", "Skywalker", "lukeskywalker@test.it", password));
    }

    @AfterAll
    static void tearDownAll() {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
    }
}