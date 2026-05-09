# Train Ticketing Application - Java Project

## 1. Solution Description

This project is a Java console application for managing train ticket bookings.

The application allows customers to search for train connections between stations, book one or multiple tickets, and receive booking confirmation messages. It also provides administrator operations for managing trains, routes, bookings, and train delays.

The project was developed using Java 17 and Maven. Data is persisted locally using JSON files, so the information remains available after restarting the application.

---

## 2. Technologies Used

- Java 17
- Maven
- Jackson Databind
- Jackson Java Time Module
- JSON file-based persistence

---

## 3. How to Run the Application

### Prerequisites

Make sure the following are installed:

- Java 17 or newer
- Maven

### Run from IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Wait for Maven dependencies to load.
3. Run the `Main` class located at:

```text
src/main/java/com/siemens/Main.java
```

### Build from Terminal

From the root folder of the project, run:

```bash
mvn clean package
```

This command compiles the project and verifies that it builds successfully.

The application can then be run from IntelliJ IDEA by running the `Main` class.

---

## 4. Project Structure

```text
src/main/java/com/siemens/
├── Main.java
├── model/
│   ├── Booking.java
│   ├── BookingStatus.java
│   ├── Passenger.java
│   ├── Route.java
│   ├── RouteStop.java
│   ├── Station.java
│   └── Train.java
├── repository/
│   ├── InMemoryDatabase.java
│   └── JsonDatabase.java
├── service/
│   ├── BookingService.java
│   ├── DataSeeder.java
│   ├── EmailService.java
│   ├── RouteService.java
│   ├── SearchService.java
│   └── TrainService.java
└── ui/
    ├── ConsoleApplication.java
    └── InputReader.java
```

### Package Responsibilities

- `model` contains the main domain classes used by the application.
- `repository` contains the in-memory storage and JSON persistence logic.
- `service` contains the business logic of the application.
- `ui` contains the console menu and user input handling.
- `Main` is the entry point of the application.

---

## 5. Main Features

### Customer Features

- Search for a train connection between two stations.
- Find direct connections.
- Find connections that require one train change.
- Book one ticket.
- Book multiple tickets.
- Prevent overbooking based on train capacity.
- Simulate sending booking confirmation emails.

### Administrator Features

- Add new trains.
- Remove trains.
- Modify train capacity.
- Add new routes.
- Add stations to routes.
- Remove stations from routes.
- Change the train assigned to a route.
- Remove routes.
- View all bookings for a specific train.
- Report train delays.
- Simulate sending delay notification emails to affected customers.

---

## 6. Data Persistence

The application uses local JSON files to save data.

On the first run, if no saved data exists, the application automatically creates demo trains and routes using the `DataSeeder` class.

After that, the data is saved in the `data/` folder:

```text
data/
├── trains.json
├── routes.json
└── bookings.json
```

The `data/` folder is generated automatically and is ignored by Git.

This means the evaluator does not need to manually create any data files. The application creates the initial demo data by itself.

---

## 7. Initial Demo Data

The application starts with the following demo trains:

```text
IR-101 | capacity: 80
R-202  | capacity: 50
IC-303 | capacity: 120
```

The demo routes include:

```text
R1: Cluj-Napoca -> Alba Iulia -> Sibiu -> Brasov
R2: Brasov -> Bucuresti
R3: Oradea -> Cluj-Napoca -> Alba Iulia -> Timisoara
```

These demo routes allow testing:

- direct connections
- connections with one train change
- unavailable connections

---

## 8. Application Menu

When the application starts, the following menu is displayed:

```text
====================================
 Train Ticketing Application
====================================
1. Search connection
2. Book one ticket
3. Book multiple tickets
4. Admin: Manage trains
5. Admin: Manage routes
6. Admin: View bookings for a train
7. Admin: Report train delay
8. Show all trains and routes
0. Exit
====================================
Choose an option:
```

---

## 9. Input and Output Examples

### 9.1 Search for a Direct Connection

Input:

```text
Choose an option: 1
Departure station: Cluj-Napoca
Arrival station: Brasov
```

Output:

```text
Direct connection found:
Train: IR-101
From: Cluj-Napoca at 08:00
To: Brasov at 14:00
```

---

### 9.2 Search for a Connection With One Train Change

Input:

```text
Choose an option: 1
Departure station: Cluj-Napoca
Arrival station: Bucuresti
```

Output:

```text
Connection with one change found:
First train: IR-101
From: Cluj-Napoca at 08:00
To change station: Brasov at 14:00

Second train: R-202
From: Brasov at 15:00
To: Bucuresti at 18:10
```

---

### 9.3 Search for an Unavailable Connection

Input:

```text
Choose an option: 1
Departure station: Bucuresti
Arrival station: Oradea
```

Output:

```text
No possible connection found between Bucuresti and Oradea.
```

---

### 9.4 Book One Ticket

Input:

```text
Choose an option: 2
Departure station: Cluj-Napoca
Arrival station: Brasov
Passenger full name: John Smith
Passenger email: john.smith@email.com
```

Output:

```text
=== EMAIL SENT ===
To: john.smith@email.com
Subject: Booking confirmation - B-1234ABCD
Hello John Smith,
Your booking has been confirmed.
Train: IR-101
From: Cluj-Napoca
To: Brasov
Booking ID: B-1234ABCD
==================

Booking completed successfully.
Booking ID: B-1234ABCD
Train: IR-101
From: Cluj-Napoca
To: Brasov
Tickets: 1
Passengers: [John Smith <john.smith@email.com>]
Status: CONFIRMED
Booking time: 2026-05-09T12:30:00
```

The booking ID and booking time are generated automatically, so they will be different each time.

---

### 9.5 Book Multiple Tickets

Input:

```text
Choose an option: 3
Departure station: Cluj-Napoca
Arrival station: Brasov
Number of passengers: 2

Passenger 1:
Full name: John Smith
Email: john.smith@email.com

Passenger 2:
Full name: Anna Brown
Email: anna.brown@email.com
```

Output:

```text
=== EMAIL SENT ===
To: john.smith@email.com
Subject: Booking confirmation - B-5678EFGH
Hello John Smith,
Your booking has been confirmed.
Train: IR-101
From: Cluj-Napoca
To: Brasov
Booking ID: B-5678EFGH
==================

=== EMAIL SENT ===
To: anna.brown@email.com
Subject: Booking confirmation - B-5678EFGH
Hello Anna Brown,
Your booking has been confirmed.
Train: IR-101
From: Cluj-Napoca
To: Brasov
Booking ID: B-5678EFGH
==================

Booking completed successfully.
Booking ID: B-5678EFGH
Train: IR-101
From: Cluj-Napoca
To: Brasov
Tickets: 2
Passengers: [John Smith <john.smith@email.com>, Anna Brown <anna.brown@email.com>]
Status: CONFIRMED
Booking time: 2026-05-09T12:35:00
```

---

### 9.6 Prevent Overbooking

If a train does not have enough available seats, the booking is rejected.

Example input:

```text
Choose an option: 3
Departure station: Cluj-Napoca
Arrival station: Brasov
Number of passengers: 2
```

Example output:

```text
Booking failed. Not enough available seats.
Train capacity: 1
Already booked seats: 0
Requested seats: 2
```

This prevents selling more tickets than the train capacity allows.

---

### 9.7 List Trains

Input:

```text
Choose an option: 4
Choose an option: 1
```

Output:

```text
Available trains:
- IR-101 | capacity: 80
- R-202 | capacity: 50
- IC-303 | capacity: 120
```

---

### 9.8 Add a Train

Input:

```text
Choose an option: 4
Choose an option: 2
Train number: IR-500
Capacity: 100
```

Output:

```text
Train added successfully: IR-500 | capacity: 100
```

---

### 9.9 Modify Train Capacity

Input:

```text
Choose an option: 4
Choose an option: 3
Train number: IR-500
New capacity: 120
```

Output:

```text
Train capacity updated successfully.
```

---

### 9.10 Remove a Train

Input:

```text
Choose an option: 4
Choose an option: 4
Train number: IR-500
```

Output:

```text
Train removed successfully.
```

---

### 9.11 List Routes

Input:

```text
Choose an option: 5
Choose an option: 1
```

Output:

```text
Available routes:
Route R1 | Train IR-101
  - Cluj-Napoca | arrival: - | departure: 08:00
  - Alba Iulia | arrival: 09:25 | departure: 09:35
  - Sibiu | arrival: 11:10 | departure: 11:20
  - Brasov | arrival: 14:00 | departure: -

Route R2 | Train R-202
  - Brasov | arrival: - | departure: 15:00
  - Bucuresti | arrival: 18:10 | departure: -
```

---

### 9.12 Add a Route

Input:

```text
Choose an option: 5
Choose an option: 2
Route ID: R4
Train number assigned to route: IR-101
```

Output:

```text
Route added successfully.
```

---

### 9.13 Add a Stop to a Route

Input:

```text
Choose an option: 5
Choose an option: 3
Route ID: R4
Station name: Deva
Arrival time: 10:00
Departure time: 10:10
```

Output:

```text
Stop added successfully to route R4.
```

For the first station of a route, the arrival time can be left empty.

For the last station of a route, the departure time can be left empty.

---

### 9.14 Remove a Stop From a Route

Input:

```text
Choose an option: 5
Choose an option: 4
Route ID: R4
Station name: Deva
```

Output:

```text
Stop removed successfully from route R4.
```

---

### 9.15 Change the Train Assigned to a Route

Input:

```text
Choose an option: 5
Choose an option: 5
Route ID: R4
New train number: IC-303
```

Output:

```text
Route train updated successfully.
```

---

### 9.16 Remove a Route

Input:

```text
Choose an option: 5
Choose an option: 6
Route ID: R4
```

Output:

```text
Route removed successfully.
```

---

### 9.17 View Bookings for a Train

Input:

```text
Choose an option: 6
Train number: IR-101
```

Output:

```text
Bookings for train IR-101:
Booking ID: B-1234ABCD
Train: IR-101
From: Cluj-Napoca
To: Brasov
Tickets: 1
Passengers: [John Smith <john.smith@email.com>]
Status: CONFIRMED
Booking time: 2026-05-09T12:30:00
```

If no bookings exist for that train, the application displays:

```text
No bookings found for train IR-101.
```

---

### 9.18 Report a Train Delay

Input:

```text
Choose an option: 7
Train number: IR-101
Delay in minutes: 20
```

Output:

```text
=== EMAIL SENT ===
To: john.smith@email.com
Subject: Train delay notification - IR-101
Hello John Smith,
We inform you that train IR-101 has a delay of 20 minutes.
==================

Delay reported successfully for train IR-101.
Affected customers notified: 1
```

The delay is saved, and all customers with bookings for that train are notified through the simulated email service.

---

### 9.19 Show All Trains and Routes

Input:

```text
Choose an option: 8
```

Output:

```text
Available trains:
- IR-101 | capacity: 80
- R-202 | capacity: 50
- IC-303 | capacity: 120

Available routes:
Route R1 | Train IR-101
  - Cluj-Napoca | arrival: - | departure: 08:00
  - Alba Iulia | arrival: 09:25 | departure: 09:35
  - Sibiu | arrival: 11:10 | departure: 11:20
  - Brasov | arrival: 14:00 | departure: -
```

---

## 10. Design Decisions

### Console Application

The project was implemented as a console application in order to keep the solution simple, easy to run, and focused on the required functionality.

A graphical interface was not added because the assignment focuses on problem solving and business logic. A console interface makes the application easier to test and evaluate.

### JSON Persistence

Instead of using a database, the application uses JSON files for persistence. This keeps the project lightweight while still allowing data to be saved between application runs.

The repository layer can be replaced later with a real database implementation if needed.

### Simulated Email Service

The application simulates email sending by printing email messages to the console.

This avoids external SMTP configuration and keeps the project easy to run on any machine. The `EmailService` class could later be replaced with a real email implementation.

### Layered Architecture

The project is split into separate packages for models, repositories, services, and user interface logic.

This improves readability and makes the application easier to maintain and extend.

---

## 11. Limitations and Possible Future Improvements

Possible future improvements include:

- Real email sending using SMTP.
- Database persistence using MySQL or PostgreSQL.
- Graphical user interface.
- Administrator authentication.
- More complex route search with multiple train changes.
- Ticket cancellation.
- Seat number assignment.
- Unit tests for service classes.

---

## 12. Notes

The application creates demo data automatically on first run.

All changes made during execution are saved locally in JSON files.

The generated `data/` folder is ignored by Git and does not need to be manually created.