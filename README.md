# Spring Boot Playground

A playground project to experiment with Spring Boot using REST and WebSockets. This project leverages reactive programming and modern Kotlin features to create an efficient and scalable backend.

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

## Contributing

Feel free to fork and submit pull requests to improve this project!

## License

This project is licensed under the MIT License.

