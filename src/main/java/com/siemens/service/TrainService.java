package com.siemens.service;

import com.siemens.model.Booking;
import com.siemens.model.Passenger;
import com.siemens.model.Train;
import com.siemens.repository.InMemoryDatabase;
import com.siemens.repository.JsonDatabase;

import java.util.List;
import java.util.Optional;

public class TrainService {
    private final InMemoryDatabase database;
    private final JsonDatabase storage;
    private final EmailService emailService;

    public TrainService(InMemoryDatabase database, JsonDatabase storage, EmailService emailService) {
        this.database = database;
        this.storage = storage;
        this.emailService = emailService;
    }

    public void addTrain(String trainNumber, int capacity) {
        if (database.trainExists(trainNumber)) {
            System.out.println("A train with this number already exists.");
            return;
        }
        Train train = new Train(trainNumber, capacity);
        database.addTrain(train);
        storage.saveDatabase(database);
        System.out.println("Train added successfully: " + train);
    }

    public void removeTrain(String trainNumber) {
        boolean removed = database.removeTrainByNumber(trainNumber);
        if (!removed) {
            System.out.println("Train not found.");
            return;
        }
        storage.saveDatabase(database);
        System.out.println("Train removed successfully.");
    }

    public void updateTrainCapacity(String trainNumber, int newCapacity) {
        Optional<Train> optionalTrain = database.findTrainByNumber(trainNumber);
        if (optionalTrain.isEmpty()) {
            System.out.println("Train not found.");
            return;
        }
        Train train = optionalTrain.get();
        train.setCapacity(newCapacity);
        storage.saveDatabase(database);
        System.out.println("Train capacity updated successfully.");
    }

    public void listTrains() {
        if (database.getTrains().isEmpty()) {
            System.out.println("No trains available.");
            return;
        }
        System.out.println("Available trains:");
        for (Train train : database.getTrains()) {
            System.out.println("- " + train);
        }
    }

    public void showBookingsForTrain(String trainNumber) {
        List<Booking> bookings = database.findBookingsByTrainNumber(trainNumber);
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for train " + trainNumber + ".");
            return;
        }
        System.out.println("Bookings for train " + trainNumber + ":");
        for (Booking booking : bookings) {
            System.out.println(booking);
            System.out.println("====================");
        }
    }

    public void reportDelay(String trainNumber, int delayMinutes) {
        Optional<Train> optionalTrain = database.findTrainByNumber(trainNumber);
        if (optionalTrain.isEmpty()) {
            System.out.println("Train not found.");
            return;
        }
        Train train = optionalTrain.get();
        train.setDelayMinutes(delayMinutes);
        storage.saveDatabase(database);
        List<Booking> affectedBookings = database.findBookingsByTrainNumber(trainNumber);
        for (Booking booking : affectedBookings) {
            for (Passenger passenger : booking.getPassengers()) {
                emailService.sendDelayNotification(passenger, train, delayMinutes);
            }
        }
        System.out.println("Delay reported successfully for train " + trainNumber + ".");
        System.out.println("Affected customers notified: " + affectedBookings.size());
    }
}