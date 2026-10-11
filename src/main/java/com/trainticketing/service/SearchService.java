package com.trainticketing.service;

import com.trainticketing.model.Route;
import com.trainticketing.model.RouteStop;
import com.trainticketing.repository.InMemoryDatabase;

import java.util.List;
import java.util.Optional;

public class SearchService {
    private final InMemoryDatabase database;

    public SearchService(InMemoryDatabase database) {
        this.database = database;
    }

    public Optional<Route> findDirectRoute(String departureStation, String arrivalStation) {
        return database.getRoutes().stream().filter(route -> route.connectsStationsInCorrectOrder(departureStation, arrivalStation)).findFirst();
    }

    public void searchConnection(String departureStation, String arrivalStation) {
        Optional<Route> directRoute = findDirectRoute(departureStation, arrivalStation);
        if (directRoute.isPresent()) {
            printDirectConnection(directRoute.get(), departureStation, arrivalStation);
            return;
        }
        boolean foundConnectionWithChange = searchConnectionWithOneChange(departureStation, arrivalStation);
        if (!foundConnectionWithChange) {
            System.out.println("No possible connection found between " + departureStation + " and " + arrivalStation + ".");
        }
    }

    private boolean searchConnectionWithOneChange(String departureStation, String arrivalStation) {
        List<Route> routes = database.getRoutes();
        for (Route firstRoute : routes) {
            if (!firstRoute.containsStation(departureStation)) {
                continue;
            }
            int departureIndex = firstRoute.getStationIndex(departureStation);
            for (int i = departureIndex + 1; i < firstRoute.getStops().size(); i++) {
                String changeStation = firstRoute.getStops().get(i).getStation().getName();
                for (Route secondRoute : routes) {
                    if (firstRoute == secondRoute) {
                        continue;
                    }
                    if (secondRoute.connectsStationsInCorrectOrder(changeStation, arrivalStation)) {
                        printConnectionWithChange(firstRoute, secondRoute, departureStation, changeStation, arrivalStation);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void printDirectConnection(Route route, String departureStation, String arrivalStation) {
        RouteStop departureStop = route.findStopByStationName(departureStation).orElseThrow();
        RouteStop arrivalStop = route.findStopByStationName(arrivalStation).orElseThrow();
        System.out.println("Direct connection found:");
        System.out.println("Train: " + route.getTrain().getTrainNumber());
        System.out.println("From: " + departureStation + " at " + departureStop.getDepartureTime());
        System.out.println("To: " + arrivalStation + " at " + arrivalStop.getArrivalTime());
    }

    private void printConnectionWithChange(Route firstRoute, Route secondRoute, String departureStation, String changeStation, String arrivalStation) {
        RouteStop departureStop = firstRoute.findStopByStationName(departureStation).orElseThrow();
        RouteStop changeArrivalStop = firstRoute.findStopByStationName(changeStation).orElseThrow();
        RouteStop changeDepartureStop = secondRoute.findStopByStationName(changeStation).orElseThrow();
        RouteStop arrivalStop = secondRoute.findStopByStationName(arrivalStation).orElseThrow();
        System.out.println("Connection with one change found:");
        System.out.println("First train: " + firstRoute.getTrain().getTrainNumber());
        System.out.println("From: " + departureStation + " at " + departureStop.getDepartureTime());
        System.out.println("To change station: " + changeStation + " at " + changeArrivalStop.getArrivalTime());
        System.out.println();
        System.out.println("Second train: " + secondRoute.getTrain().getTrainNumber());
        System.out.println("From: " + changeStation + " at " + changeDepartureStop.getDepartureTime());
        System.out.println("To: " + arrivalStation + " at " + arrivalStop.getArrivalTime());
    }
}