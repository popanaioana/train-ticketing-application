package com.siemens.repository;

import com.siemens.model.Booking;
import com.siemens.model.Route;
import com.siemens.model.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryDatabase {
    private List<Train> trains;
    private List<Route> routes;
    private List<Booking> bookings;

    public InMemoryDatabase() {
        this.trains = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public List<Train> getTrains() {
        return trains;
    }

    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addTrain(Train train) {
        trains.add(train);
    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public Optional<Train> findTrainByNumber(String trainNumber) {
        return trains.stream().filter(train -> train.getTrainNumber().equalsIgnoreCase(trainNumber)).findFirst();
    }

    public Optional<Route> findRouteById(String routeId) {
        return routes.stream().filter(route -> route.getRouteId().equalsIgnoreCase(routeId)).findFirst();
    }

    public List<Booking> findBookingsByTrainNumber(String trainNumber) {
        return bookings.stream().filter(booking -> booking.getTrain().getTrainNumber().equalsIgnoreCase(trainNumber)).toList();
    }

    public boolean removeTrainByNumber(String trainNumber) {
        return trains.removeIf(train -> train.getTrainNumber().equalsIgnoreCase(trainNumber));
    }

    public boolean removeRouteById(String routeId) {
        return routes.removeIf(route -> route.getRouteId().equalsIgnoreCase(routeId));
    }

    public boolean trainExists(String trainNumber) {
        return findTrainByNumber(trainNumber).isPresent();
    }

    public boolean routeExists(String routeId) {
        return findRouteById(routeId).isPresent();
    }
}