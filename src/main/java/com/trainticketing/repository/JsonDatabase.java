
package com.trainticketing.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trainticketing.model.Booking;
import com.trainticketing.model.Route;
import com.trainticketing.model.Train;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class JsonDatabase {

    private final Path dataFolder;
    private final ObjectMapper objectMapper;

    public JsonDatabase() {
        this(Path.of("data"));
    }

    public JsonDatabase(Path dataFolder) {
        this.dataFolder = dataFolder;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveDatabase(InMemoryDatabase database) {
        try {
            Files.createDirectories(dataFolder);
            writeList("trains.json", database.getTrains());
            writeList("routes.json", database.getRoutes());
            writeList("bookings.json", database.getBookings());
        } catch (IOException e) {
            throw new DatabaseException("Failed to save database.", e);
        }
    }

    public InMemoryDatabase loadDatabase() {
        try {
            Files.createDirectories(dataFolder);
            InMemoryDatabase database = new InMemoryDatabase();
            database.setTrains(readList("trains.json", new TypeReference<List<Train>>() {}));
            database.setRoutes(readList("routes.json", new TypeReference<List<Route>>() {}));
            database.setBookings(readList("bookings.json", new TypeReference<List<Booking>>() {}));
            return database;
        } catch (IOException e) {
            throw new DatabaseException("Failed to load database.", e);
        }
    }

    public boolean hasSavedData() {
        return Files.exists(dataFolder.resolve("trains.json")) && Files.exists(dataFolder.resolve("routes.json")) && Files.exists(dataFolder.resolve("bookings.json"));
    }

    private <T> List<T> readList(String fileName, TypeReference<List<T>> typeReference) throws IOException {
        Path file = dataFolder.resolve(fileName);
        if (!Files.exists(file)) {
            throw new IOException("Missing database file: " + fileName);
        }
        if (Files.size(file) == 0) {
            throw new IOException("Empty database file: " + fileName);
        }
        List<T> result = objectMapper.readValue(file.toFile(), typeReference);
        if (result == null) {
            throw new IOException("Invalid database content: " + fileName);
        }
        return result;
    }

    private void writeList(String fileName, Object data) throws IOException {
        Path target = dataFolder.resolve(fileName);
        Path temporary = Files.createTempFile(dataFolder, "db-", ".tmp");
        try {
            objectMapper.writeValue(temporary.toFile(), data);
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
