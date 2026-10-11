
# Train Ticketing Application

[![Java CI with Maven](https://github.com/popanaioana/train-ticketing-application/actions/workflows/maven.yml/badge.svg)](https://github.com/popanaioana/train-ticketing-application/actions/workflows/maven.yml)

A Java 17 console-based train ticketing system that allows passengers to search for train connections, book tickets, and receive simulated booking confirmations. The application also provides administrative functionality for managing trains, routes, reservations, and delays.

The project follows a layered architecture, implements segment-based seat availability calculations, and uses JSON files for persistent storage.

## Technologies

- **Java 17** – Core application and business logic
- **Maven** – Dependency management and build automation
- **Jackson** – JSON serialization and deserialization
- **JUnit 5** – Automated unit and integration tests
- **GitHub Actions** – Continuous integration
- **Git** – Version control

## Features

### Passenger Features

- Search for direct train connections between stations.
- Find connections requiring one train change.
- Book single or multiple tickets.
- Check seat availability for a specific journey.
- Prevent overbooking through segment-based capacity calculations.
- Generate unique booking identifiers.
- Receive simulated booking confirmation emails.

### Administrator Features

- Add, remove, and manage trains.
- Update train capacities.
- Create and modify train routes.
- Add or remove stations from routes.
- Assign trains to routes.
- View reservations for a specific train.
- Report train delays.
- Simulate delay notifications for affected passengers.

## Segment-Based Seat Allocation

One of the main features of the application is its segment-based seat availability algorithm.

Instead of treating a booked seat as occupied for the entire train route, the system calculates availability separately for each segment of the requested journey.

For example, consider a train with a capacity of **2 seats**:

```text
Cluj-Napoca ---- Alba Iulia ---- Sibiu
```

If two passengers book tickets from Cluj-Napoca to Alba Iulia, both seats are occupied only on that segment.

```text
Segment                         Occupied    Available
----------------------------------------------------
Cluj-Napoca -> Alba Iulia          2            0
Alba Iulia -> Sibiu                0            2
```

Therefore, two other passengers can still book tickets from Alba Iulia to Sibiu.

### Algorithm

The booking service:

1. Identifies the departure and arrival station indices.
2. Retrieves confirmed bookings associated with the train.
3. Calculates occupied seats for every segment of the requested journey.
4. Determines the maximum occupancy across those segments.
5. Subtracts the maximum occupancy from the train capacity.
6. Rejects requests that exceed the available capacity.

This approach allows seats to be reused on non-overlapping journeys while preventing overbooking.

## Architecture

The application follows a layered architecture that separates domain models, data persistence, business logic, and user interaction.

```text
src/
├── main/
│   └── java/
│       └── com/trainticketing/
│           ├── Main.java
│           ├── model/
│           │   ├── Booking.java
│           │   ├── BookingStatus.java
│           │   ├── Passenger.java
│           │   ├── Route.java
│           │   ├── RouteStop.java
│           │   ├── Station.java
│           │   └── Train.java
│           ├── repository/
│           │   ├── DatabaseException.java
│           │   ├── InMemoryDatabase.java
│           │   └── JsonDatabase.java
│           ├── service/
│           │   ├── BookingException.java
│           │   ├── BookingService.java
│           │   ├── DataSeeder.java
│           │   ├── EmailService.java
│           │   ├── RouteService.java
│           │   ├── SearchService.java
│           │   └── TrainService.java
│           └── ui/
│               ├── ConsoleApplication.java
│               └── InputReader.java
└── test/
    └── java/
        └── com/trainticketing/
            ├── repository/
            │   └── JsonDatabaseTest.java
            └── service/
                └── BookingServiceTest.java
```

### Package Responsibilities

| Package | Responsibility |
|---------|----------------|
| `model` | Domain entities representing trains, routes, passengers, and bookings |
| `repository` | In-memory data management and JSON persistence |
| `service` | Business logic, route searching, booking validation, and notifications |
| `ui` | Console menus and user interaction |

## Data Persistence

Application data is stored locally using Jackson and JSON files.

```text
data/
├── trains.json
├── routes.json
└── bookings.json
```

On the first execution, the application automatically generates sample trains and routes through the `DataSeeder` class.

Subsequent executions load the previously saved data.

### Persistence Reliability

The application includes several mechanisms for improving data reliability:

- Validation of missing or corrupted JSON files.
- Custom exceptions for persistence errors.
- Temporary files and atomic file replacement where supported.
- In-memory rollback when saving a newly created booking fails.
- Booking confirmations sent only after successful persistence.

**Limitation:** Each JSON file is saved separately. Although individual file replacements are atomic where supported, saving all three files is not a single atomic transaction.

## Testing

The project includes **13 automated tests** using JUnit 5.

### Booking Service Tests

Tests cover:

- Seat availability when no bookings exist.
- Seat reuse across consecutive route segments.
- Overlapping reservations.
- Fully booked segments.
- Invalid station order.
- Unknown stations.
- Successful booking persistence.
- Rollback when persistence fails.

### JSON Persistence Tests

Tests cover:

- Creating database files.
- Saving and loading application data.
- Handling corrupted JSON.
- Handling missing database files.
- Creating nested storage directories.

### Run Tests

From the project root:

```bash
mvn test
```

To perform a complete build verification:

```bash
mvn clean verify
```

## Continuous Integration

The project uses **GitHub Actions** to automatically build and test the application.

The workflow runs on:

- Pushes to the `main` branch.
- Pull requests targeting `main`.

The CI pipeline:

1. Checks out the repository.
2. Sets up Java 17.
3. Configures Maven dependency caching.
4. Runs `mvn --batch-mode clean verify`.

This helps detect compilation errors and test failures before changes are merged.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or newer
- Apache Maven
- IntelliJ IDEA or another Java-compatible IDE (optional)

### Clone the Repository

```bash
git clone https://github.com/popanaioana/train-ticketing-application.git
cd train-ticketing-application
```

### Build the Project

```bash
mvn clean package
```

### Run the Application

1. Open the project in IntelliJ IDEA.
2. Wait for Maven dependencies to load.
3. Navigate to:

```text
src/main/java/com/trainticketing/Main.java
```

4. Run the `Main` class.

The application automatically initializes demo data when no saved database exists.

## Example Usage

### Search for a Connection

```text
Choose an option: 1
Departure station: Cluj-Napoca
Arrival station: Brasov
```

Example result:

```text
Direct connection found:
Train: IR-101
From: Cluj-Napoca at 08:00
To: Brasov at 14:00
```

### Book a Ticket

```text
Choose an option: 2
Departure station: Cluj-Napoca
Arrival station: Brasov
Passenger full name: John Smith
Passenger email: john.smith@example.com
```

After successful booking, the application generates a unique booking ID, persists the reservation, and prints a simulated confirmation email.

## Design Decisions

### Layered Architecture

Separating the application into models, repositories, services, and UI components improves maintainability and testability.

### JSON-Based Storage

JSON persistence keeps the project lightweight and allows the application to run without an external database server.

### Custom Exception Handling

`BookingException` and `DatabaseException` distinguish booking validation failures from storage-related errors.

### Simulated Email Notifications

Email confirmations and delay notifications are printed to the console rather than sent through an external SMTP service.

## Limitations and Future Improvements

- Support for travel dates and multiple departures of the same train.
- Transactional persistence using a relational database.
- Real email notifications through SMTP or an email API.
- User authentication and role-based authorization.
- Ticket cancellation and modification.
- Individual seat assignments.
- Support for more complex multi-transfer journeys.
- Additional automated tests and concurrency protection.
- A graphical or web-based user interface.

## Project Status

The application is functional and includes automated tests and a GitHub Actions CI workflow.

It was developed as a Java software engineering project, with a focus on object-oriented programming, business logic, data persistence, error handling, and automated testing.
