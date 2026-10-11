package com.trainticketing.service;

import com.trainticketing.model.Route;
import com.trainticketing.model.RouteStop;
import com.trainticketing.model.Station;
import com.trainticketing.model.Train;
import com.trainticketing.repository.InMemoryDatabase;
import com.trainticketing.repository.JsonDatabase;

import java.time.LocalTime;
import java.util.Optional;

public class RouteService {
    private final InMemoryDatabase database;
    private final JsonDatabase storage;

    public RouteService(InMemoryDatabase database, JsonDatabase storage) {
        this.database = database;
        this.storage = storage;
    }

    public void addRoute(String routeId, String trainNumber) {
        if (database.routeExists(routeId)) {
            System.out.println("A route with this ID already exists.");
            return;
        }
        Optional<Train> optionalTrain = database.findTrainByNumber(trainNumber);
        if (optionalTrain.isEmpty()) {
            System.out.println("Train not found. Please create the train before assigning a route.");
            return;
        }
        Route route = new Route(routeId, optionalTrain.get());
        database.addRoute(route);
        storage.saveDatabase(database);
        System.out.println("Route added successfully.");
    }

    public void addStopToRoute(String routeId, String stationName, LocalTime arrivalTime, LocalTime departureTime) {
        Optional<Route> optionalRoute = database.findRouteById(routeId);
        if (optionalRoute.isEmpty()) {
            System.out.println("Route not found.");
            return;
        }
        Route route = optionalRoute.get();
        route.addStop(new RouteStop(new Station(stationName), arrivalTime, departureTime));
        storage.saveDatabase(database);
        System.out.println("Stop added successfully to route " + routeId + ".");
    }

    public void removeRoute(String routeId) {
        boolean removed = database.removeRouteById(routeId);
        if (!removed) {
            System.out.println("Route not found.");
            return;
        }
        storage.saveDatabase(database);
        System.out.println("Route removed successfully.");
    }

    public void changeRouteTrain(String routeId, String newTrainNumber) {
        Optional<Route> optionalRoute = database.findRouteById(routeId);
        Optional<Train> optionalTrain = database.findTrainByNumber(newTrainNumber);
        if (optionalRoute.isEmpty()) {
            System.out.println("Route not found.");
            return;
        }
        if (optionalTrain.isEmpty()) {
            System.out.println("Train not found.");
            return;
        }
        Route route = optionalRoute.get();
        route.setTrain(optionalTrain.get());
        storage.saveDatabase(database);
        System.out.println("Route train updated successfully.");
    }

    public void removeStopFromRoute(String routeId, String stationName) {
        Optional<Route> optionalRoute = database.findRouteById(routeId);
        if (optionalRoute.isEmpty()) {
            System.out.println("Route not found.");
            return;
        }
        Route route = optionalRoute.get();
        boolean removed = route.removeStopByStationName(stationName);
        if (!removed) {
            System.out.println("Station not found on this route.");
            return;
        }
        storage.saveDatabase(database);
        System.out.println("Stop removed successfully from route " + routeId + ".");
    }

    public void listRoutes() {
        if (database.getRoutes().isEmpty()) {
            System.out.println("No routes available.");
            return;
        }
        System.out.println("Available routes:");
        for (Route route : database.getRoutes()) {
            System.out.println(route);
        }
    }
}