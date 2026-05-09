package com.siemens.service;

import com.siemens.model.Booking;
import com.siemens.model.Passenger;
import com.siemens.model.Route;
import com.siemens.model.Train;
import com.siemens.repository.InMemoryDatabase;
import com.siemens.repository.JsonDatabase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookingService {
    private final InMemoryDatabase database;
    private final JsonDatabase storage;
    private final EmailService emailService;
    private final SearchService searchService;

    public BookingService(InMemoryDatabase database, JsonDatabase storage, EmailService emailService, SearchService searchService) {
        this.database = database;
        this.storage = storage;
        this.emailService = emailService;
        this.searchService = searchService;
    }

    public void bookTickets(String departureStation, String arrivalStation, List<Passenger> passengers) {
        Optional<Route> optionalRoute = searchService.findDirectRoute(departureStation, arrivalStation);
        if (optionalRoute.isEmpty()) {
            System.out.println("Booking failed. Tickets can currently be booked only for direct routes.");
            System.out.println("Please search for a valid direct connection first.");
            return;
        }
        Route route = optionalRoute.get();
        Train train = route.getTrain();
        int alreadyBookedSeats = getBookedSeatsForTrain(train.getTrainNumber());
        int requestedSeats = passengers.size();
        if (alreadyBookedSeats + requestedSeats > train.getCapacity()) {
            System.out.println("Booking failed. Not enough available seats.");
            System.out.println("Train capacity: " + train.getCapacity());
            System.out.println("Already booked seats: " + alreadyBookedSeats);
            System.out.println("Requested seats: " + requestedSeats);
            return;
        }
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, train, departureStation, arrivalStation, passengers);
        database.addBooking(booking);
        storage.saveDatabase(database);
        for (Passenger passenger : passengers) {
            emailService.sendBookingConfirmation(passenger, booking);
        }
        System.out.println("Booking completed successfully.");
        System.out.println(booking);
    }

    public int getBookedSeatsForTrain(String trainNumber) {
        return database.findBookingsByTrainNumber(trainNumber).stream().mapToInt(Booking::getNumberOfTickets).sum();
    }

    public int getAvailableSeatsForTrain(String trainNumber) {
        Optional<Train> optionalTrain = database.findTrainByNumber(trainNumber);
        if (optionalTrain.isEmpty()) {
            return 0;
        }
        Train train = optionalTrain.get();
        return train.getCapacity() - getBookedSeatsForTrain(trainNumber);
    }

    private String generateBookingId() {
        return "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}