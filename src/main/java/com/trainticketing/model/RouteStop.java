package com.trainticketing.model;

import java.time.LocalTime;

public class RouteStop {
    private Station station;
    private LocalTime arrivalTime;
    private LocalTime departureTime;

    public RouteStop() {

    }

    public RouteStop(Station station, LocalTime arrivalTime, LocalTime departureTime) {
        this.station = station;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public Station getStation() {
        return station;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    @Override
    public String toString() {
        return station.getName() + " | arrival: " + (arrivalTime == null ? "-" : arrivalTime) + " | departure: " + (departureTime == null ? "-" : departureTime);
    }
}