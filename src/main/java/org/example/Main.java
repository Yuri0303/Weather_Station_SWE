package org.example;

import org.example.businesslogic.*;
import org.example.domainmodel.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final double repairChance = 0.8; //I manutentori hanno una probabilità dell'80% di riparare un sensore, altrimenti lo cambiano
    private static SystemUser loggedUser = null;

    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        AdminDatabaseController adminDatabaseController = new AdminDatabaseController();
        adminDatabaseController.resetDatabase();
        adminDatabaseController.createDatabase();
        try {
            adminDatabaseController.defaultInstances();
        } catch (SQLException e) {}

        SensorManager theSensorManager = new SensorManager();//thread
        SensorMonitor theSensorMonitor = new SensorMonitor();
        SystemMonitor theSystemMonitor = new SystemMonitor();//thread

        theSensorManager.attach(theSensorMonitor);//costruzione dell'observer
        theSystemMonitor.start();

        Thread theSensorManagerThread = new Thread(theSensorManager);
        theSensorManagerThread.start();

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

    private static float askForFloat(String text){
        String input;

        while (true) {
            System.out.print(text);
            input = scanner.nextLine();
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                System.out.println("Input invalido, si prega di inserire un numero, sono ammessi anche numeri decimale.");
            }
        }
    }

    private static LocalDateTime askForDate(String text, boolean startOfDay) {
        String input;
        while (true) {
            System.out.print(text + " (dd-mm-yyyy): ");
            input = scanner.nextLine();
            try {
                if(startOfDay)
                    return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-uuuu")).atStartOfDay();
                else
                    return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-uuuu")).atTime(23,59,59);
            } catch (DateTimeParseException e) {
                System.out.println("Input invalido, si prega di riprovare");
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

    public static void handleAction(){
        while (true) {
            int index = chooseMenuOption("  WEATHER STATION", new String[]{"Utente", "Personale", "Esci"});
            switch (index) {
                case 1:
                    handleUser();
                    break;
                case 2:
                    handleStaff();
                    break;
                case 3:
                    System.exit(0);
            }
        }
    }

    public static void handleStaff(){
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

    public static void handleStaffLogin(String role){
        loggedUser = null;
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
                    handleAdminViewMeasurementsHistory();
                    break;
                case 2:
                    handleViewUsers();
                    break;
                case 3:
                    loggedUser = null;
                    return;
            }
        }
    }
    public static void handleAdminViewMeasurementsHistory() {
        LocalDateTime startDate;
        LocalDateTime endDate;
        while (true) {
            startDate = askForDate("Inserire data di inizio", true);
            endDate = askForDate("Inserire data di fine", false);

            if (startDate.isAfter(endDate)) {
                System.out.println("Date con ordine invertito, si prega di reinserire le date.");
            } else {
                break;
            }
        }
        AdminController adminController = new AdminController();
        ArrayList<Measurement> measurements = adminController.readDataHistory(startDate, endDate);
        System.out.printf("%-15s %-15s %s%n", "ID Sensore", "Valore", "Data");
        for (Measurement m : measurements) {
            System.out.println(m.toString());
        }
        if (!measurements.isEmpty() && chooseYesOrNo("Aprire un ticket su un sensore?")) {
            try {
                DatabaseMutex.mutex.acquire();
                int sensorId = askForInteger("Inserire ID sensore: ");
                try {
                    adminController.openTicket(sensorId);
                } catch (SQLException e) {}
                finally {
                    DatabaseMutex.mutex.release();
                }
            }catch (InterruptedException e){System.err.println("Main interrotto");}

        }
    }
    public static void handleViewUsers() {
        AdminController adminController = new AdminController();
        ArrayList<User> users = adminController.viewUsers();
        System.out.printf("%-4s %-30s %-30s %-50s %s%n", "ID", "Nome", "Cognome", "Email", "Bloccato");
        for (User u : users) {
            System.out.println(u.toString());
        }
        if (!users.isEmpty() && chooseYesOrNo("Vuoi bloccare un utente?")) {
            int userId = askForInteger("Inserire ID utente: ");
            adminController.blockUser(userId);
        }
    }

    //MAINTAINER DASHBOARD
    public static void handleMaintainerAction() {
        while(true){
            int index = chooseMenuOption("  DASHBOARD MANUTENTORE", new String[]{"Visualizza ticket aperti", "Ripara e chiudi ticket", "Logout"});
            switch (index){
                case 1:
                    handleViewOpenTicket();
                    break;
                case 2:
                    handleRepairSensor();
                    break;
                case 3:
                    loggedUser = null;
                    return;
            }
        }
    }

    public static void handleViewOpenTicket(){
        MaintainerController maintainerController = new MaintainerController();
        ArrayList<Ticket> openTickets = maintainerController.viewOpenTickets();
        System.out.printf("%-15s %-15s %s%n", "ID Ticket", "ID Sensore", "Preso");
        for (Ticket t : openTickets)
            System.out.println(t.toString());

        if(!openTickets.isEmpty() && chooseYesOrNo("Vuoi prendere un ticket?")){
            try{
                DatabaseMutex.mutex.acquire();
                int ticketId = askForInteger("Inserire l'id del ticket che si desidera prendere: ");
                try {
                    maintainerController.takeTicket(ticketId, loggedUser.getId());
                }catch (SQLException e){}
                finally {
                    DatabaseMutex.mutex.release();
                }
            }catch (InterruptedException e){
                System.err.println("Main interrotto");
            }

        }
    }

    public static void handleRepairSensor(){//include la possibilità che il sensore venga sostituito e include anche la chiusura del ticket a riparazione o sostituzione completata
        MaintainerController maintainerController = new MaintainerController();
        maintainerController.repairSensor(repairChance, askForInteger("Inserisci l'id del ticket da te acquisito"), loggedUser.getId());
    }

    //USER DASHBOARD
    public static void handleUser() {
        int index = chooseMenuOption("  AUTENTICAZIONE / REGISTRAZIONE UTENTE", new String[]{"Registrati", "Accedi", "Indietro", "Esci"});
        switch (index) {
            case 1:
                handleUserRegistration();
                break;
            case 2:
                handleUserLogin();
                break;
            case 3:
                return;
            case 4:
                System.exit(0);
        }
    }

    public static void handleUserRegistration() {
        boolean success = false;
        while (!success) {
            System.out.print("Nome: ");
            String firstname = scanner.nextLine();
            System.out.print("Cognome: ");
            String lastname = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            UserLoginController userLoginController = new UserLoginController();
            success = userLoginController.register(firstname, lastname, email, password);
            if (!success) {
                if(!chooseYesOrNo("Registrazione fallita. Vuoi ritentare?"))
                    break;
            }
            System.out.println("Registrazione avvenuta con successo");
        }
        handleUser();
    }

    public static void handleUserLogin() {
        loggedUser = null;
        System.out.print("\nEmail: ");
        String email = scanner.nextLine();
        System.out.print("\nPassword: ");
        String password = scanner.nextLine();
        UserLoginController userLoginController = new UserLoginController();
        loggedUser = userLoginController.login(email, password);
        if(loggedUser != null)
            handleUserAction();
    }

    public static void handleUserAction() {
        while (true) {
            int index = chooseMenuOption("  DASHBOARD UTENTE", new String[]{"Lettura dati", "Imposta Alert Rule", "Visualizza notifiche non lette", "Logout"});
            switch (index) {
                case 1:
                    handleUserViewMeasurement();
                    break;
                case 2:
                    handleSetAlertRule();
                    break;
                case 3:
                    handleViewUnreadNotifications();
                    break;
                case 4:
                    loggedUser = null;
                    return;
            }
        }
    }

    public static void handleUserViewMeasurement() {
        try {
            DatabaseMutex.mutex.acquire();
            UserController userController = new UserController();
            ArrayList<Measurement> measurements = userController.readData();
            System.out.printf("%-15s %-15s %s%n", "ID Sensore", "Valore", "Data");
            for (Measurement m : measurements) {
                System.out.println(m.toString());
            }
            if (chooseYesOrNo("Vuoi vedere lo storico?")) {
                LocalDateTime startDate, endDate;
                while (true) {
                    startDate = askForDate("Inserire data di inizio", true);
                    endDate = askForDate("Inserire data di fine", false);

                    if (startDate.isAfter(endDate)) {
                        System.out.println("Date con ordine invertito, si prega di reinserire le date.");
                    } else {
                        break;
                    }
                }
                measurements = userController.readDataHistory(startDate, endDate);
                System.out.printf("%-15s %-15s %s%n", "ID Sensore", "Valore", "Data");
                for (Measurement m : measurements) {
                    System.out.println(m.toString());
                }
            }
            DatabaseMutex.mutex.release();
        } catch (InterruptedException e) {
            System.err.println("Main interrotto");
        }
    }

    public static void handleSetAlertRule() {
        SensorType[] types = SensorType.values();
        String[] typesS = new String[types.length];
        for (int i = 0; i < types.length; i++)
            typesS[i] = types[i].toString();
        int index = chooseMenuOption("  Impostare il tipo di sensore: ", typesS);

        SensorType chosenType = null;
        switch (index){
            case 1:
                chosenType = types[0];
                break;
            case 2:
                chosenType = types[1];
                break;
            case 3:
                chosenType = types[2];
                break;
            case 4:
                chosenType = types[3];
                break;
        }

        index = chooseMenuOption("  Selezionare la modalità di impostazione: ", new String[]{"Solo lower bound", "Solo upper bound", "Sia lower che upper bound"});
        float lowerBound, upperBound;
        UserController userController = new UserController();
        switch (index){
            case 1:
                lowerBound = askForFloat("Inserire il limite inferiore: ");
                userController.setAlertRule(loggedUser.getId(), chosenType, lowerBound, null);
                break;
            case 2:
                upperBound = askForFloat("Inserire il limite superiore: ");
                userController.setAlertRule(loggedUser.getId(), chosenType, null, upperBound);
                break;
            case 3:
                lowerBound = askForFloat("Inserire il limite inferiore: ");
                upperBound = askForFloat("Inserire il limite superiore: ");
                userController.setAlertRule(loggedUser.getId(), chosenType, lowerBound, upperBound);
                break;
        }
    }

    public static void handleViewUnreadNotifications() {
        try {
            DatabaseMutex.mutex.acquire();
            UserController userController = new UserController();
            ArrayList<Notification> notifications = userController.viewUnreadNotifications(loggedUser.getId());
            System.out.printf("%-15s %-300s %s%n", "ID Notifica", "Messaggio", "Data");
            for (Notification n : notifications)
                System.out.println(n.toString());

            if(chooseYesOrNo("Vuoi leggere anche lo storico delle notifiche?")){
                int lastDays = askForInteger("Inserire il numero dei giorni passati dei quali si vuol leggere le notifiche: ");
                notifications = userController.viewNotificationHistory(loggedUser.getId(), lastDays);
                System.out.printf("%-15s %-300s %s%n", "ID Notifica", "Messaggio", "Data");
                for (Notification n : notifications)
                    System.out.println(n.toString());
            }
            DatabaseMutex.mutex.release();
        }catch (InterruptedException e){
            System.err.println("Main interrotto");
        }

    }
}