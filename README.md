# Playlist Management API

Technical exercise developed with Java Spring Boot.

## Overview

This project implements a music playlist management system exposing a REST API.

Beyond the basic CRUD operations, the objective was to design a maintainable and extensible architecture using common software engineering principles and design patterns.

The application supports playlist management, intelligent shuffling, song recommendations, export formats and a global playback controller.

---

## Main Features

### Song Management

* Create songs
* Retrieve songs
* List all songs
* Delete songs

### Playlist Management

* Create playlists
* Retrieve playlists
* Update playlist metadata
* Delete playlists
* Add songs to playlists
* Remove songs from playlists
* Maintain playlist ordering through positions

### Shuffle Strategies

Three shuffle algorithms are available:

#### Random Shuffle

Applies a standard random shuffle.

#### Smart Shuffle

Attempts to avoid consecutive songs from the same artist.

A greedy approach is used: for each position, the algorithm tries to select a song whose artist differs from the previously selected one.

When the playlist distribution is highly unbalanced, artist repetitions may still occur because no valid alternative remains.

#### Genre Balanced Shuffle

Distributes songs as evenly as possible between genres.

The algorithm groups songs by genre and alternates between genres while songs remain available.

### Recommendation Engine

Recommendations are generated from playlist content.

The current implementation:

1. Determines the dominant genre of the playlist
2. Finds songs belonging to that genre
3. Excludes songs already present in the playlist
4. Returns up to three recommendations

Future improvements could leverage playlist metadata (name and description) or listening history.

### Export System

The export system was designed using the Strategy pattern.

Currently supported formats:

* JSON
* M3U

Adding a new export format only requires implementing a new exporter strategy and registering it in the factory.

### Global Music Player

A global player manages playback state across the application.

Supported operations:

* Play playlist
* Play single song
* Pause
* Resume
* Stop
* Next song
* Previous song
* Retrieve current status

The player is implemented as a Spring singleton service.

Thread safety is ensured through synchronized access to the shared player state.

---

## Architecture

### Layered Architecture

The project follows a classical Spring architecture:

Controller
↓
Service
↓
Repository
↓
Database

Responsibilities are clearly separated:

* Controllers expose REST endpoints
* Services implement business logic
* Repositories handle persistence
* Mappers isolate entity/DTO transformations

### Design Patterns

#### Strategy Pattern

Used for:

* Shuffle algorithms
* Export formats

This allows new behaviors to be introduced without modifying existing code.

#### Factory Pattern

Factories are used to resolve the appropriate strategy implementation at runtime.

#### Singleton

The global music player is implemented as a singleton Spring bean.

---

## Testing

The project contains:

* Service unit tests
* Controller integration tests
* End-to-end lifecycle integration tests

One integration scenario validates the complete workflow:

1. Create songs
2. Create playlist
3. Add songs
4. Shuffle playlist
5. Generate recommendations
6. Add recommended songs
7. Play playlist
8. Stop playback
9. Play a single song

---

## Technologies

* Java 17
* Spring Boot
* Spring Data JPA
* H2 Database
* Lombok
* MapStruct
* OpenAPI / Swagger
* JUnit 5

---

## Running the Application

```bash
./mvnw spring-boot:run
```

---

## Swagger

http://localhost:8080/swagger-ui.html

---

## H2 Console

http://localhost:8080/h2-console

---

## Author

Alexandre Bonvarlet
