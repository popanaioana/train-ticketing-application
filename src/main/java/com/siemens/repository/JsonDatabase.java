package com.siemens.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.siemens.model.Booking;
import com.siemens.model.Route;
import com.siemens.model.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabase {
    private static final String DATA_FOLDER = "data";
    private static final String TRAINS_FILE = DATA_FOLDER + "/trains.json";
    private static final String ROUTES_FILE = DATA_FOLDER + "/routes.json";
    private static final String BOOKINGS_FILE = DATA_FOLDER + "/bookings.json";

    private final ObjectMapper objectMapper;

    public JsonDatabase() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveDatabase(InMemoryDatabase database) {
        createDataFolderIfMissing();
        try {
            objectMapper.writeValue(new File(TRAINS_FILE), database.getTrains());
            objectMapper.writeValue(new File(ROUTES_FILE), database.getRoutes());
            objectMapper.writeValue(new File(BOOKINGS_FILE), database.getBookings());
        } catch (IOException e) {
            System.out.println("Error while saving database: " + e.getMessage());
        }
    }

    public InMemoryDatabase loadDatabase() {
        createDataFolderIfMissing();
        InMemoryDatabase database = new InMemoryDatabase();
        database.setTrains(readListFromFile(TRAINS_FILE, new TypeReference<List<Train>>() {}));
        database.setRoutes(readListFromFile(ROUTES_FILE, new TypeReference<List<Route>>() {}));
        database.setBookings(readListFromFile(BOOKINGS_FILE, new TypeReference<List<Booking>>() {}));
        return database;
    }

    public boolean hasSavedData() {
        return new File(TRAINS_FILE).exists() && new File(ROUTES_FILE).exists() && new File(BOOKINGS_FILE).exists();
    }

    private <T> List<T> readListFromFile(String filePath, TypeReference<List<T>> typeReference) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.out.println("Error while reading file " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void createDataFolderIfMissing() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}