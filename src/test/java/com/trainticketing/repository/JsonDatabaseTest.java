
package com.trainticketing.repository;

import com.trainticketing.model.Route;
import com.trainticketing.model.Train;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonDatabaseTest {

    @TempDir
    Path tempDir;

    private JsonDatabase jsonDatabase;
    private InMemoryDatabase database;

    @BeforeEach
    void setUp() {
        jsonDatabase = new JsonDatabase(tempDir);
        database = new InMemoryDatabase();
    }

    @Test
    void shouldCreateAllJsonFilesWhenSaving() {
        jsonDatabase.saveDatabase(database);
        assertTrue(Files.exists(tempDir.resolve("trains.json")));
        assertTrue(Files.exists(tempDir.resolve("routes.json")));
        assertTrue(Files.exists(tempDir.resolve("bookings.json")));
    }

    @Test
    void shouldLoadSavedTrainsAndRoutes() {
        Train train = new Train("IR100", 100);
        Route route = new Route("R100", train);
        database.addTrain(train);
        database.addRoute(route);
        jsonDatabase.saveDatabase(database);
        InMemoryDatabase loaded = jsonDatabase.loadDatabase();
        assertEquals(1, loaded.getTrains().size());
        assertEquals(1, loaded.getRoutes().size());
        assertEquals("IR100", loaded.getTrains().get(0).getTrainNumber());
        assertEquals(100, loaded.getTrains().get(0).getCapacity());
        assertEquals("R100", loaded.getRoutes().get(0).getRouteId());
        assertEquals("IR100", loaded.getRoutes().get(0).getTrain().getTrainNumber());
    }

    @Test
    void shouldThrowExceptionForCorruptedJson() throws IOException {
        jsonDatabase.saveDatabase(database);
        Path trainsFile = tempDir.resolve("trains.json");
        Files.writeString(trainsFile, "{ invalid json }");
        assertThrows(DatabaseException.class, () -> jsonDatabase.loadDatabase());
        assertEquals("{ invalid json }", Files.readString(trainsFile));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseFileIsMissing() throws IOException {
        jsonDatabase.saveDatabase(database);
        Files.delete(tempDir.resolve("routes.json"));
        assertFalse(jsonDatabase.hasSavedData());
        assertThrows(DatabaseException.class, () -> jsonDatabase.loadDatabase());
    }

    @Test
    void shouldCreateDataDirectoryAutomatically() {
        Path newDirectory = tempDir.resolve("nested/data");
        JsonDatabase newStorage = new JsonDatabase(newDirectory);
        assertFalse(Files.exists(newDirectory));
        newStorage.saveDatabase(database);
        assertTrue(Files.isDirectory(newDirectory));
        assertTrue(Files.exists(newDirectory.resolve("trains.json")));
    }
}
