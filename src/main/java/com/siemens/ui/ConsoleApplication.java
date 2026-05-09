package com.siemens.ui;

import com.siemens.model.Passenger;
import com.siemens.repository.InMemoryDatabase;
import com.siemens.repository.JsonDatabase;
import com.siemens.service.BookingService;
import com.siemens.service.DataSeeder;
import com.siemens.service.EmailService;
import com.siemens.service.RouteService;
import com.siemens.service.SearchService;
import com.siemens.service.TrainService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsoleApplication {
    private final InputReader inputReader;
    private InMemoryDatabase database;
    private JsonDatabase storage;
    private EmailService emailService;
    private SearchService searchService;
    private BookingService bookingService;
    private TrainService trainService;
    private RouteService routeService;

    public ConsoleApplication() {
        this.inputReader = new InputReader();
    }

    public void start() {
        initializeApplication();
        runMainMenu();
    }

    private void initializeApplication() {
        storage = new JsonDatabase();
        if (storage.hasSavedData()) {
            database = storage.loadDatabase();
            System.out.println("Saved data loaded successfully.");
        } else {
            database = new InMemoryDatabase();
            DataSeeder dataSeeder = new DataSeeder();
            dataSeeder.seed(database);
            storage.saveDatabase(database);
            System.out.println("Demo data created successfully.");
        }
        emailService = new EmailService();
        searchService = new SearchService(database);
        bookingService = new BookingService(database, storage, emailService, searchService);
        trainService = new TrainService(database, storage, emailService);
        routeService = new RouteService(database, storage);
    }

    private void runMainMenu() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int option = inputReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> searchConnection();
                case 2 -> bookSingleTicket();
                case 3 -> bookMultipleTickets();
                case 4 -> manageTrainsMenu();
                case 5 -> manageRoutesMenu();
                case 6 -> showBookingsForTrain();
                case 7 -> reportTrainDelay();
                case 8 -> listAllData();
                case 0 -> {
                    storage.saveDatabase(database);
                    System.out.println("Application closed. Data saved.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println(" Train Ticketing Application");
        System.out.println("====================================");
        System.out.println("1. Search connection");
        System.out.println("2. Book one ticket");
        System.out.println("3. Book multiple tickets");
        System.out.println("4. Admin: Manage trains");
        System.out.println("5. Admin: Manage routes");
        System.out.println("6. Admin: View bookings for a train");
        System.out.println("7. Admin: Report train delay");
        System.out.println("8. Show all trains and routes");
        System.out.println("0. Exit");
        System.out.println("====================================");
    }

    private void searchConnection() {
        System.out.println();
        System.out.println("=== Search Connection ===");
        String departureStation = inputReader.readString("Departure station: ");
        String arrivalStation = inputReader.readString("Arrival station: ");
        searchService.searchConnection(departureStation, arrivalStation);
    }

    private void bookSingleTicket() {
        System.out.println();
        System.out.println("=== Book One Ticket ===");
        String departureStation = inputReader.readString("Departure station: ");
        String arrivalStation = inputReader.readString("Arrival station: ");
        String fullName = inputReader.readString("Passenger full name: ");
        String email = inputReader.readString("Passenger email: ");
        Passenger passenger = new Passenger(fullName, email);
        bookingService.bookTickets(departureStation, arrivalStation, List.of(passenger));
    }

    private void bookMultipleTickets() {
        System.out.println();
        System.out.println("=== Book Multiple Tickets ===");
        String departureStation = inputReader.readString("Departure station: ");
        String arrivalStation = inputReader.readString("Arrival station: ");
        int numberOfPassengers = inputReader.readInt("Number of passengers: ");
        if (numberOfPassengers <= 0) {
            System.out.println("Number of passengers must be greater than 0.");
            return;
        }
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 1; i <= numberOfPassengers; i++) {
            System.out.println();
            System.out.println("Passenger " + i + ":");

            String fullName = inputReader.readString("Full name: ");
            String email = inputReader.readString("Email: ");

            passengers.add(new Passenger(fullName, email));
        }
        bookingService.bookTickets(departureStation, arrivalStation, passengers);
    }

    private void manageTrainsMenu() {
        boolean back = false;
        while (!back) {
            printTrainMenu();
            int option = inputReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> trainService.listTrains();
                case 2 -> addTrain();
                case 3 -> updateTrainCapacity();
                case 4 -> removeTrain();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printTrainMenu() {
        System.out.println();
        System.out.println("========== Manage Trains ==========");
        System.out.println("1. List trains");
        System.out.println("2. Add train");
        System.out.println("3. Update train capacity");
        System.out.println("4. Remove train");
        System.out.println("0. Back");
        System.out.println("===================================");
    }

    private void addTrain() {
        System.out.println();
        System.out.println("=== Add Train ===");
        String trainNumber = inputReader.readString("Train number: ");
        int capacity = inputReader.readInt("Capacity: ");
        if (capacity <= 0) {
            System.out.println("Capacity must be greater than 0.");
            return;
        }
        trainService.addTrain(trainNumber, capacity);
    }

    private void updateTrainCapacity() {
        System.out.println();
        System.out.println("=== Update Train Capacity ===");
        String trainNumber = inputReader.readString("Train number: ");
        int newCapacity = inputReader.readInt("New capacity: ");
        if (newCapacity <= 0) {
            System.out.println("Capacity must be greater than 0.");
            return;
        }
        trainService.updateTrainCapacity(trainNumber, newCapacity);
    }

    private void removeTrain() {
        System.out.println();
        System.out.println("=== Remove Train ===");
        String trainNumber = inputReader.readString("Train number: ");
        trainService.removeTrain(trainNumber);
    }

    private void removeStopFromRoute() {
        System.out.println();
        System.out.println("=== Remove Stop From Route ===");
        String routeId = inputReader.readString("Route ID: ");
        String stationName = inputReader.readString("Station name: ");
        routeService.removeStopFromRoute(routeId, stationName);
    }

    private void manageRoutesMenu() {
        boolean back = false;
        while (!back) {
            printRouteMenu();
            int option = inputReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> routeService.listRoutes();
                case 2 -> addRoute();
                case 3 -> addStopToRoute();
                case 4 -> removeStopFromRoute();
                case 5 -> changeRouteTrain();
                case 6 -> removeRoute();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printRouteMenu() {
        System.out.println();
        System.out.println("========== Manage Routes ==========");
        System.out.println("1. List routes");
        System.out.println("2. Add route");
        System.out.println("3. Add stop to route");
        System.out.println("4. Remove stop from route");
        System.out.println("5. Change train assigned to route");
        System.out.println("6. Remove route");
        System.out.println("0. Back");
        System.out.println("===================================");
    }

    private void addRoute() {
        System.out.println();
        System.out.println("=== Add Route ===");
        String routeId = inputReader.readString("Route ID: ");
        String trainNumber = inputReader.readString("Train number assigned to route: ");
        routeService.addRoute(routeId, trainNumber);
    }

    private void addStopToRoute() {
        System.out.println();
        System.out.println("=== Add Stop To Route ===");
        String routeId = inputReader.readString("Route ID: ");
        String stationName = inputReader.readString("Station name: ");
        System.out.println("Use HH:mm format.");
        System.out.println("For first station, arrival time can be empty.");
        System.out.println("For last station, departure time can be empty.");
        LocalTime arrivalTime = inputReader.readOptionalTime("Arrival time: ");
        LocalTime departureTime = inputReader.readOptionalTime("Departure time: ");
        routeService.addStopToRoute(routeId, stationName, arrivalTime, departureTime);
    }

    private void changeRouteTrain() {
        System.out.println();
        System.out.println("=== Change Route Train ===");
        String routeId = inputReader.readString("Route ID: ");
        String trainNumber = inputReader.readString("New train number: ");
        routeService.changeRouteTrain(routeId, trainNumber);
    }

    private void removeRoute() {
        System.out.println();
        System.out.println("=== Remove Route ===");
        String routeId = inputReader.readString("Route ID: ");
        routeService.removeRoute(routeId);
    }

    private void showBookingsForTrain() {
        System.out.println();
        System.out.println("=== Bookings For Train ===");
        String trainNumber = inputReader.readString("Train number: ");
        trainService.showBookingsForTrain(trainNumber);
    }

    private void reportTrainDelay() {
        System.out.println();
        System.out.println("=== Report Train Delay ===");
        String trainNumber = inputReader.readString("Train number: ");
        int delayMinutes = inputReader.readInt("Delay in minutes: ");
        if (delayMinutes < 0) {
            System.out.println("Delay cannot be negative.");
            return;
        }
        trainService.reportDelay(trainNumber, delayMinutes);
    }

    private void listAllData() {
        System.out.println();
        trainService.listTrains();
        System.out.println();
        routeService.listRoutes();
    }
}