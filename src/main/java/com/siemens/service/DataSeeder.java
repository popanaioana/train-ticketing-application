package com.siemens.service;

import com.siemens.model.Route;
import com.siemens.model.RouteStop;
import com.siemens.model.Station;
import com.siemens.model.Train;
import com.siemens.repository.InMemoryDatabase;

import java.time.LocalTime;

public class DataSeeder {

    public void seed(InMemoryDatabase database) {
        if (!database.getTrains().isEmpty() || !database.getRoutes().isEmpty()) {
            return;
        }
        Station cluj = new Station("Cluj-Napoca");
        Station alba = new Station("Alba Iulia");
        Station sibiu = new Station("Sibiu");
        Station brasov = new Station("Brasov");
        Station bucuresti = new Station("Bucuresti");
        Station oradea = new Station("Oradea");
        Station timisoara = new Station("Timisoara");
        Train train1 = new Train("IR-101", 80);
        Train train2 = new Train("R-202", 50);
        Train train3 = new Train("IC-303", 120);
        database.addTrain(train1);
        database.addTrain(train2);
        database.addTrain(train3);
        Route route1 = new Route("R1", train1);
        route1.addStop(new RouteStop(cluj, null, LocalTime.of(8, 0)));
        route1.addStop(new RouteStop(alba, LocalTime.of(9, 25), LocalTime.of(9, 35)));
        route1.addStop(new RouteStop(sibiu, LocalTime.of(11, 10), LocalTime.of(11, 20)));
        route1.addStop(new RouteStop(brasov, LocalTime.of(14, 0), null));
        Route route2 = new Route("R2", train2);
        route2.addStop(new RouteStop(brasov, null, LocalTime.of(15, 0)));
        route2.addStop(new RouteStop(bucuresti, LocalTime.of(18, 10), null));
        Route route3 = new Route("R3", train3);
        route3.addStop(new RouteStop(oradea, null, LocalTime.of(7, 30)));
        route3.addStop(new RouteStop(cluj, LocalTime.of(10, 0), LocalTime.of(10, 20)));
        route3.addStop(new RouteStop(alba, LocalTime.of(12, 0), LocalTime.of(12, 10)));
        route3.addStop(new RouteStop(timisoara, LocalTime.of(16, 40), null));
        database.addRoute(route1);
        database.addRoute(route2);
        database.addRoute(route3);
    }
}