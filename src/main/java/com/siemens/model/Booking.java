package com.siemens.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private String bookingId;
    private Train train;
    private String departureStation;
    private String arrivalStation;
    private List<Passenger> passengers;
    private LocalDateTime bookingTime;
    private BookingStatus status;

    public Booking() {
    }

    public Booking(String bookingId, Train train, String departureStation, String arrivalStation, List<Passenger> passengers) {
        this.bookingId = bookingId;
        this.train = train;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.passengers = passengers;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public int getNumberOfTickets() {
        return passengers == null ? 0 : passengers.size();
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + System.lineSeparator()
                + "Train: " + train.getTrainNumber() + System.lineSeparator()
                + "From: " + departureStation + System.lineSeparator()
                + "To: " + arrivalStation + System.lineSeparator()
                + "Tickets: " + getNumberOfTickets() + System.lineSeparator()
                + "Passengers: " + passengers + System.lineSeparator()
                + "Status: " + status + System.lineSeparator()
                + "Booking time: " + bookingTime;
    }
}