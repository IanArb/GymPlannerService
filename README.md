# Gym Planner playground project

A playground project to experiment with Spring Boot using REST and WebSockets. This project leverages reactive programming and modern Kotlin features to create an efficient and scalable backend.

This service provides a scalable and reactive backend for managing gym operations, including personal training bookings, fitness class management, and reporting faulty gym equipment.

## Technologies Used

- **Spring Boot WebFlux** - For building reactive REST APIs and WebSockets
- **MongoDB** - NoSQL database for data persistence
- **Kotlin** - Concise, expressive, and modern JVM language
- **Coroutines** - Asynchronous programming with structured concurrency
- **MockK** - Kotlin-first mocking library for unit testing
- **Flapdoodle Embedded MongoDB** - Lightweight in-memory MongoDB for testing
- **JSON Web Token (JWT)** - Secure authentication and authorization

## Features

- Reactive API using WebFlux
- WebSocket support for real-time communication
- Asynchronous data processing with coroutines
- JWT-based authentication
- Embedded MongoDB for easier testing

## Getting Started

### Prerequisites

Ensure you have the following installed:

- JDK 17+
- Docker (optional, for running MongoDB)
- Gradle or Maven

### Running the Application

1. Clone the repository:
   ```sh
   git clone git@github.com:IanArb/GymPlannerService.git
   ```
2. Start MongoDB (if not using embedded MongoDB):
   ```sh
   docker run -d --name mongodb -p 27017:27017 mongo
   ```
3. Run the application:
   ```sh
   ./gradlew bootRun
   ```

### Running Tests

Run tests using:
```sh
./gradlew test
```

### OpenAPI Endpoints

This project automatically generates OpenAPI 3.0 documentation using Springdoc OpenAPI.

Swagger UI: http://localhost:8080/webjars/swagger-ui/index.html

Swagger UI provides an interactive interface to view and test your API endpoints.

OpenAPI JSON Spec: http://localhost:8080/v3/api-docs

This endpoint serves the raw OpenAPI documentation in JSON format.

These endpoints are available once the application is running. You can explore the API, try requests directly from Swagger UI, and see the API documentation in real-time.

## Contributing

Feel free to fork and submit pull requests to improve this project!

## License

This project is licensed under the MIT License.

