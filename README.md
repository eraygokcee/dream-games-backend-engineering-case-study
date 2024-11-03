# Dream Games Backend Engineering Case Study

This project implements a basic REST API for a mobile game, where users can register, participate in tournaments, and claim rewards. The API is developed using Java with Spring Boot and includes layered architecture for maintainability and scalability.

## Project Structure

```plaintext
src/
├── com.dreamgames.backendengineeringcasestudy
│   ├── dto
│   ├── exceptions
│   ├── redisconfig
│   ├── tournament
│   │   ├── controller
│   │   ├── helper
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   ├── users(same with tournamentLayer)
│   └── BackendEngineeringCaseStudyApplication.java
└── resources/
    ├── application.properties
```
# Tournament Application

## Key Components

- **DTOs (Data Transfer Objects):** Classes for managing API responses.
- **Exceptions:** Custom exception classes and a global exception handler to manage error responses.
- **Redis Configuration:** The `RedisConfig` class handles caching configurations, improving response time and reducing database load.
- **Tournament Module:** Manages tournament operations with controllers, services, repositories, and helpers to handle tasks like joining tournaments, fetching leaderboards, and claiming rewards.
- **User Module:** Handles user-related operations, including registration, level updates, and retrieving user details.

## Functionality Overview

### User Management
- **Create User:** Registers a new user with an initial level and coin balance. Users are randomly assigned to one of five countries (Turkey, United States, United Kingdom, France, or Germany).
- **Update Level:** Updates a user’s level and adds coins after successfully completing a level.
- **Retrieve User:** Fetches the current information of a user, including level, coins, and country.

### Tournament System
The application includes a daily tournament system where users compete in groups representing different countries. Key functionalities include:

- **Enter Tournament:** Users with sufficient level and coins can join an active tournament. Each group includes five users from different countries.
- **Claim Reward:** Users can claim rewards based on their tournament performance.

### Leaderboard Management
- **Group Leaderboard:** Shows the user’s ranking within their tournament group.
- **Country Leaderboard:** Displays cumulative scores of users from each country in the tournament.

### Redis for Caching
Redis is integrated to cache frequently accessed data, enhancing performance by reducing load on the MySQL database.

## Architectural Decisions

The application follows a layered architecture:

- **Controller Layer:** Exposes RESTful endpoints for user and tournament operations.
- **Service Layer:** Contains business logic and interacts with the repository layer.
- **Repository Layer:** Manages data persistence in MySQL and caching in Redis.

This structure improves readability and maintainability and allows independent development and testing of each part.

## Unit Testing

Unit tests are implemented using mock data to avoid database dependency, ensuring reliable and fast tests by isolating business logic.

## API Documentation

A Postman collection for testing the API endpoints is included in this repository. You can import the [Postman Collection](https://documenter.getpostman.com/view/31565073/2sAY4xB2kv) to view and test the endpoints.

## Running the Application

### Steps

#### Database Setup
MySQL is used within Docker, and tables are created using `mysql-db-dump.sql`.

#### Run with Docker:
```
docker-compose build
docker-compose up
```
#### Manuel Start(without Docker)
```
./mvnw spring-boot:run
```
Docker commands start both the MySQL ,Redis database and the Spring Boot application, allowing them to communicate with each other.
