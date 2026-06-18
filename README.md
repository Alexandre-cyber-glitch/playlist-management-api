# Playlist Management API

Technical exercise built with Spring Boot.

## Features

- Playlist CRUD operations
- Song management
- Multiple shuffle strategies
    - Random
    - Smart (avoid artist repetition)
    - Genre Balanced
- Song recommendations
- Playlist export
    - JSON
    - M3U
- Thread-safe global music player

## Technologies

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Lombok
- MapStruct
- OpenAPI / Swagger
- JUnit 5

## Architecture

### Design Patterns

- Strategy Pattern for shuffle algorithms
- Strategy Pattern for export formats
- Singleton Spring Bean for the global music player

## Run

```bash
./mvnw spring-boot:run
```

## Swagger

```
http://localhost:8080/swagger-ui.html
```

## H2 Console

```
http://localhost:8080/h2-console
```

## Author

Alexandre Bonvarlet