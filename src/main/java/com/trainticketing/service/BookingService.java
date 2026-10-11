package com.trainticketing.service;

import com.trainticketing.model.Booking;
import com.trainticketing.model.Passenger;
import com.trainticketing.model.Route;
import com.trainticketing.model.Train;
import com.trainticketing.repository.DatabaseException;
import com.trainticketing.repository.InMemoryDatabase;
import com.trainticketing.repository.JsonDatabase;
import com.trainticketing.model.BookingStatus;

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

    public Booking bookTickets(String departureStation, String arrivalStation, List<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            throw new BookingException("At least one passenger is required.");
        }
        if (passengers.stream().anyMatch(p -> p == null)) {
            throw new BookingException("Passenger information cannot be null.");
        }
        Route route = searchService.findDirectRoute(departureStation, arrivalStation).orElseThrow(() -> new BookingException("No direct route found between " + departureStation + " and " + arrivalStation));
        Train train = route.getTrain();
        int availableSeats = getAvailableSeatsForJourney(route, departureStation, arrivalStation);
        int requestedSeats = passengers.size();
        if (requestedSeats > availableSeats) {
            throw new BookingException("Not enough available seats. Available: " + availableSeats + ", requested: " + requestedSeats);
        }
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, train, departureStation, arrivalStation, List.copyOf(passengers));
        database.addBooking(booking);
        try {
            storage.saveDatabase(database);
        } catch (DatabaseException e) {
            database.getBookings().remove(booking);
            throw new BookingException("Booking could not be saved. Please try again.", e);
        }
        if (emailService != null) {
            for (Passenger passenger : passengers) {
                try {
                    emailService.sendBookingConfirmation(passenger, booking);
                } catch (RuntimeException e) {
                    System.err.println("Booking saved, but confirmation failed for: " + passenger.getEmail());
                }
            }
        }
        return booking;
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

    public int getAvailableSeatsForJourney(Route route, String departureStation, String arrivalStation) {
        if (route == null || route.getTrain() == null) {
            throw new BookingException("Invalid route.");
        }
        int startIndex = route.getStationIndex(departureStation);
        int endIndex = route.getStationIndex(arrivalStation);
        if (startIndex < 0 || endIndex <= startIndex) {
            throw new BookingException("Invalid journey stations.");
        }
        int capacity = route.getTrain().getCapacity();
        int maxOccupiedSeats = 0;
        List<Booking> bookings = database.findBookingsByTrainNumber(route.getTrain().getTrainNumber());
        for (int segment = startIndex; segment < endIndex; segment++) {
            int occupiedSeats = 0;
            for (Booking booking : bookings) {
                if (booking.getStatus() != BookingStatus.CONFIRMED) {
                    continue;
                }
                int bookingStart = route.getStationIndex(booking.getDepartureStation());
                int bookingEnd = route.getStationIndex(booking.getArrivalStation());
                if (bookingStart < 0 || bookingEnd <= bookingStart) {
                    continue;
                }
                if (bookingStart <= segment && segment < bookingEnd) {
                    occupiedSeats += booking.getNumberOfTickets();
                }
            }
            maxOccupiedSeats = Math.max(maxOccupiedSeats, occupiedSeats);
        }
        return Math.max(0, capacity - maxOccupiedSeats);
    }

    private String generateBookingId() {
        return "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}