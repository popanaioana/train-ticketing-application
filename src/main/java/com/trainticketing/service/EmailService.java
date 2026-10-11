package com.trainticketing.service;

import com.trainticketing.model.Booking;
import com.trainticketing.model.Passenger;
import com.trainticketing.model.Train;

public class EmailService {

    public void sendBookingConfirmation(Passenger passenger, Booking booking) {
        System.out.println();
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To: " + passenger.getEmail());
        System.out.println("Subject: Booking confirmation - " + booking.getBookingId());
        System.out.println("Hello " + passenger.getFullName() + ",");
        System.out.println("Your booking has been confirmed.");
        System.out.println("Train: " + booking.getTrain().getTrainNumber());
        System.out.println("From: " + booking.getDepartureStation());
        System.out.println("To: " + booking.getArrivalStation());
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("==================");
        System.out.println();
    }

    public void sendDelayNotification(Passenger passenger, Train train, int delayMinutes) {
        System.out.println();
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To: " + passenger.getEmail());
        System.out.println("Subject: Train delay notification - " + train.getTrainNumber());
        System.out.println("Hello " + passenger.getFullName() + ",");
        System.out.println("We inform you that train " + train.getTrainNumber() + " has a delay of " + delayMinutes + " minutes.");
        System.out.println("==================");
        System.out.println();
    }
}