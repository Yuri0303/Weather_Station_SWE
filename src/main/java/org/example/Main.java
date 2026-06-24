package org.example;

import org.example.businesslogic.AdminController;
import org.example.businesslogic.AdminDatabaseController;
import org.example.businesslogic.StaffLoginController;
import org.example.domainmodel.Measurement;
import org.example.domainmodel.SystemUser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final double repairChance = 0.8; //I manutentori hanno una probabilità dell'80% di riparare un sensore, altrimenti lo cambiano

    private static SystemUser loggedUser = null;

    private static final Scanner scanner = new Scanner(System.in);
    //TODO: sincronizza con semaforo così che solo un thread alla volta può accedere al database
    public static void main(String[] args) {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
        adminDatabaseController.createDatabase();
        try {
            adminDatabaseController.defaultInstances();
        } catch (SQLException e) {}

        //TODO: ricordasi di attachare il sensorMonitor al sensorManager
        try {
            handleAction();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static boolean chooseYesOrNo(String text) {
        String input;
        while (true) {
            System.out.println(text + "(y/n)");
            input = scanner.nextLine();
            switch (input) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Input invalido, si prega di riprovare.");
            }
        }
    }

    private static int askForInteger(String text) {
        String input;
        while (true) {
            System.out.print(text);
            input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input invalido, si prega di inserire un numero intero.");
            }

        }
    }

    private static int chooseMenuOption(String title, String[] options) {
        String input;
        int index;

        while (true) {
            System.out.println();
            System.out.println(title);
            System.out.println();
            for (int i = 0; i < options.length; i++) {
                System.out.printf(" [%d]. %s\n", i+1, options[i]);
            }
            System.out.println();
            System.out.print(">>> ");
            input = scanner.nextLine();
            try {
                index = Integer.parseInt(input);
                if (index >= 1 && index <= options.length)
                    return index;
            } catch (NumberFormatException e) {
                System.out.println("Input invalido, si prega di riprovare.");
            }
        }
    }

    public static void handleAction() throws Exception {
        while (true) {
            int index = chooseMenuOption("  WEATHER STATION", new String[]{"Utente", "Personale", "Esci"});
            switch (index) {
                case 1:
                    handleUserLogin();
                    break;
                case 2:
                    handleStaff();
                    break;
                case 3:
                    System.exit(0);
            }
        }
    }

    public static void handleStaff() throws Exception {
        int index = chooseMenuOption("  RUOLO", new String[]{"Admin", "Manutentore", "Indietro", "Esci"});
        switch (index) {
            case 1:
                handleStaffLogin("ADMIN");
                break;
            case 2:
                handleStaffLogin("MANUTENTORE");
                break;
            case 3:
                return;
            case 4:
                System.exit(0);
        }
    }

    public static void handleStaffLogin(String role) throws Exception {
        int index = chooseMenuOption("  LOGIN" + role, new String[]{"Accedi", "Indietro", "Esci"});
        switch (index) {
            case 1:
                System.out.print("\nEmail: ");
                String email = scanner.nextLine();
                System.out.print("\nPassword: ");
                String password = scanner.nextLine();
                StaffLoginController staffLoginController = new StaffLoginController();
                if (Objects.equals(role, "ADMIN")) {
                    loggedUser = staffLoginController.adminLogin(email, password);
                    if (loggedUser != null) {
                        handleAdminAction();
                    } else {
                        System.out.println("Email o Password errati.");
                    }
                } else {
                    loggedUser = staffLoginController.maintainerLogin(email, password);
                    if (loggedUser != null) {
                        handleMaintainerAction();
                    } else {
                        System.out.println("Email o Password errati.");
                    }
                }
                break;
            case 2:
                return;
            case 3:
                System.exit(0);
        }
    }

    public static void handleAdminAction() {
        while (true) {
            int index = chooseMenuOption("  DASHBOARD ADMIN", new String[]{"Lettura storico misurazioni", "Visualizza utenti", "Logout"});
            switch (index) {
                case 1:
                    handleViewMeasurementsHistory();
                case 2:
                    handleViewUsers();
                case 3:
                    return;
            }
        }
    }

    public static void handleViewMeasurementsHistory() {
        String input;
        LocalDateTime startDate;
        LocalDateTime endDate;
        while (true) {
            while (true) {
                System.out.print("Inserire data di inizio (dd-mm-yyyy):  ");
                input = scanner.nextLine();
                try {
                    startDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-uuuu")).atStartOfDay();
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Input invalido, si prega di riprovare");
                }
            }

            while (true) {
                System.out.print("Inserire data di fine (dd-mm-yyyy):  ");
                input = scanner.nextLine();
                try {
                    endDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-uuuu")).atStartOfDay();
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Input invalido, si prega di riprovare");
                }
            }
            if (startDate.isAfter(endDate)) {
                System.out.println("Date con ordine invertito, si prega di reinserire le date.");
            } else {
                break;
            }
        }
        AdminController adminController = new AdminController();
        ArrayList<Measurement> measurements = adminController.readDataHistory(startDate, endDate);
        System.out.println(measurements.toString());
        if (!measurements.isEmpty() && chooseYesOrNo("Aprire un ticket su un sensore?")) {
            int sensorId = askForInteger("Inserire ID sensore: ");
            try {
                adminController.openTicket(sensorId);
            } catch (SQLException e) {}
        }
    }

    public static void handleViewUsers() {

    }

    public static void handleMaintainerAction() {

    }

    public static void handleUserLogin() {

    }
}
