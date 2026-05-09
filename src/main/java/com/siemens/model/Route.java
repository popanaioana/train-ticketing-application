package com.siemens.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Route {
    private String routeId;
    private Train train;
    private List<RouteStop> stops;

    public Route() {

    }

    public Route(String routeId, Train train) {
        this.routeId = routeId;
        this.train = train;
        this.stops = new ArrayList<>();
    }

    public String getRouteId() {
        return routeId;
    }

    public Train getTrain() {
        return train;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void addStop(RouteStop stop) {
        stops.add(stop);
    }

    public boolean containsStation(String stationName) {
        return stops.stream().anyMatch(stop -> stop.getStation().getName().equalsIgnoreCase(stationName));
    }

    public Optional<RouteStop> findStopByStationName(String stationName) {
        return stops.stream().filter(stop -> stop.getStation().getName().equalsIgnoreCase(stationName)).findFirst();
    }

    public int getStationIndex(String stationName) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStation().getName().equalsIgnoreCase(stationName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean connectsStationsInCorrectOrder(String departureStation, String arrivalStation) {
        int departureIndex = getStationIndex(departureStation);
        int arrivalIndex = getStationIndex(arrivalStation);
        return departureIndex != -1 && arrivalIndex != -1 && departureIndex < arrivalIndex;
    }

    public boolean removeStopByStationName(String stationName) {
        return stops.removeIf(stop -> stop.getStation().getName().equalsIgnoreCase(stationName));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Route ").append(routeId).append(" | Train ").append(train.getTrainNumber()).append(System.lineSeparator());
        for (RouteStop stop : stops) {
            builder.append("  - ").append(stop).append(System.lineSeparator());
        }
        return builder.toString();
    }
}