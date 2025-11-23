Film Society Movie API

A robust REST API for managing a movie database, built with Spring Boot and SQLite. This application was created to help the local film society digitize their records of movies, actors, and genres.

## Features

- Manage Data: Create, Read, Update, and Delete (CRUD) for Movies, Actors, and Genres.
- Search & Filter: Find movies by title, genre, release year, or actor.
- Smart Deletion: Prevents accidental deletion of genres/actors if they are linked to movies (with a force=true override).
- Pagination: Handles large lists of movies efficiently using page and size parameters.
- Auto-Data Loading: Automatically imports sample data from CSV files on startup if the database is empty.

## Technologies Used

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- SQLite (Database)
- Maven (Build Tool)

## How to Run

### Prerequisites
- Java 17 or newer installed.
- Git installed.

### Steps
1. Clone the repository:
   git clone https://gitea.kood.tech/mihhailzujev/movie-api.git

   cd movie-api

2. Run the application:
   Windows:
   mvn clean install
   .\mvnw spring-boot:run

   Mac/Linux:
   mvn clean install
   ./mvnw spring-boot:run

3. Access the API:
   The server will start on http://localhost:8080

## API Endpoints

### Movies
- Get All (Paginated): GET /api/movies?page=0&size=10
- Filter by Genre: GET /api/movies?genre=1
- Search by Title: GET /api/movies/search?title=Matrix
- Create Movie: POST /api/movies
- Delete Movie: DELETE /api/movies/{id}

### Actors
- Get All: GET /api/actors
- Create Actor: POST /api/actors
- Delete Actor: DELETE /api/actors/{id}?force=true (Use force=true if they have movies)

### Genres
- Get All: GET /api/genres
- Create Genre: POST /api/genres

## Project Structure

- src/main/java/.../entity: Database models (Movie, Actor, Genre).
- src/main/java/.../repository: Interfaces for database access.
- src/main/java/.../service: Business logic and rules.
- src/main/java/.../controller: API endpoints.
- src/main/resources/data: CSV files for initial data loading.

## Testing

You can test the API using Postman. Import the collection or simply send requests to http://localhost:8080/api/...