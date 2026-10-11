
package com.trainticketing.service;

import com.trainticketing.model.*;
import com.trainticketing.repository.InMemoryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

import com.trainticketing.repository.JsonDatabase;
import com.trainticketing.repository.DatabaseException;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class BookingServiceTest {

    private InMemoryDatabase database;
    private BookingService bookingService;
    private Route route;
    private Train train;

    @BeforeEach
    void setUp() {
        database = new InMemoryDatabase();
        train = new Train("IR100", 2);
        database.addTrain(train);
        route = new Route("R100", train);
        route.addStop(new RouteStop(new Station("Cluj"), null, null));
        route.addStop(new RouteStop(new Station("Alba Iulia"), null, null));
        route.addStop(new RouteStop(new Station("Sibiu"), null, null));
        database.addRoute(route);
        bookingService = new BookingService(database,null,null, new SearchService(database));
    }

    private void addBooking(String id, String departure, String arrival, int numberOfPassengers) {
        List<Passenger> passengers = IntStream.range(0, numberOfPassengers).mapToObj(i -> new Passenger("Passenger " + i,"passenger" + i + "@example.com")).toList();
        database.addBooking(new Booking(id, train, departure, arrival, passengers));
    }

    @Test
    void shouldReturnFullCapacityWhenNoBookingsExist() {
        assertEquals(2, bookingService.getAvailableSeatsForJourney(route, "Cluj", "Sibiu"));
    }

    @Test
    void shouldReuseSeatsOnConsecutiveSegments() {
        addBooking("B1", "Cluj", "Alba Iulia", 2);
        assertEquals(2, bookingService.getAvailableSeatsForJourney(route, "Alba Iulia", "Sibiu"));
    }

    @Test
    void shouldDetectOverlappingBookings() {
        addBooking("B1", "Cluj", "Sibiu", 1);
        assertEquals(1, bookingService.getAvailableSeatsForJourney(route, "Alba Iulia", "Sibiu"));
    }

    @Test
    void shouldReturnZeroWhenSegmentIsFullyBooked() {
        addBooking("B1", "Cluj", "Alba Iulia", 2);
        assertEquals(0, bookingService.getAvailableSeatsForJourney(route, "Cluj", "Sibiu"));
    }

    @Test
    void shouldRejectInvalidStationOrder() {
        assertThrows(BookingException.class, () -> bookingService.getAvailableSeatsForJourney(route, "Sibiu", "Cluj"));
    }

    @Test
    void shouldRejectUnknownStations() {
        assertThrows(BookingException.class, () -> bookingService.getAvailableSeatsForJourney(route, "Brasov", "Sibiu"));
    }

    @Test
    void shouldSaveBookingSuccessfully(@TempDir Path tempDir) {
        JsonDatabase storage = new JsonDatabase(tempDir);
        BookingService service = new BookingService(database, storage,null, new SearchService(database));
        Passenger passenger = new Passenger("Ana Pop","ana@example.com");
        Booking booking = service.bookTickets("Cluj","Sibiu", List.of(passenger));
        assertNotNull(booking);
        assertEquals(1, database.getBookings().size());
        assertEquals(1, booking.getNumberOfTickets());
        assertTrue(Files.exists(tempDir.resolve("bookings.json")));
        InMemoryDatabase loadedDatabase = storage.loadDatabase();
        assertEquals(1, loadedDatabase.getBookings().size());
        assertEquals(booking.getBookingId(), loadedDatabase.getBookings().get(0).getBookingId());
    }

    @Test
    void shouldRollbackBookingWhenSavingFails(@TempDir Path tempDir) throws IOException {
        Path invalidDirectory = tempDir.resolve("not-a-directory");
        Files.createFile(invalidDirectory);
        JsonDatabase storage = new JsonDatabase(invalidDirectory);
        BookingService service = new BookingService(database, storage,null, new SearchService(database));
        Passenger passenger = new Passenger("Ana Pop","ana@example.com");
        int bookingsBefore = database.getBookings().size();
        BookingException exception = assertThrows(BookingException.class, () -> service.bookTickets("Cluj","Sibiu", List.of(passenger)));
        assertEquals(bookingsBefore, database.getBookings().size());
        assertInstanceOf(DatabaseException.class, exception.getCause());
    }
}
