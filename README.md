Film Society Movie API

A robust REST API for managing a movie database, built with Spring Boot and SQLite. 

## Features

- Manage Data: Create, Read, Update, and Delete (CRUD) for Movies, Actors, and Genres.
- Search & Filter: Find movies by title, genre, release year, or actor.
- Smart Deletion: Prevents accidental deletion of genres/actors if they are linked to movies (with a force=true override).
- Pagination: Handles large lists of movies efficiently using page and size parameters.
- Auto-Data Loading: Automatically imports sample data from CSV files on startup if the database is empty.

## Software you need

- Java 17 (downgrade if you have 21 or 25)
- Spring Boot 3 (Web, Data JPA, Validation)
- SQLite (Database)
- Maven (Build Tool)
- Git

## How to Run


### Steps
1. Clone the repository:
   git clone https://gitea.kood.tech/mihhailzujev/movie-api.git

   cd movie-api

2. Run the application:
   Windows:
   mvn clean install
   mvn spring-boot:run

   Mac/Linux:
   mvn clean install
   mvn spring-boot:run

3. Access the API:
   The server will start on http://localhost:8080


## Collections

Open Postman, find "Import" button. 
Import file named "MZ_movie_db_postman_collection.json". 


## API Endpoints

The base URL for all requests is http://localhost:8080.

### MOVIES (BASE PATH: /api/movies)

- Get All (Paginated): GET /api/movies?page={PAGE}&size={SIZE}
- Filter by Genre: GET /api/movies?genre={ID}
- Search by Title: GET /api/movies/search?title={TITLE}

- Add New Movie: POST /api/movies
  * Requires JSON body: {"title": "Film Title", "releaseYear": 2024, "genres": [ {"id": 1} ], "actors": [ {"id": 5} ]}

- Delete Movie: DELETE /api/movies/{ID}

### ACTORS (BASE PATH: /api/actors)

- Get All: GET /api/actors
- Find by Name (case insensitive): GET /api/actors?name={NAME}
- Create Actor: POST /api/actors
  * Requires JSON body: {"name": "New Actor", "birthDate": "1990-01-01"}

- Update Name/Date: PATCH /api/actors/{ID}
  * Requires JSON body: {"birthDate": "1995-12-25"}

- Update Movies (Replace All): PATCH /api/actors/{ID}
  * Requires JSON body: {"movies": [ {"id": 1}, {"id": 3} ]}

- Delete Actor (Forced): DELETE /api/actors/{ID}?force=true

### GENRES (BASE PATH: /api/genres)

- Get All: GET /api/genres
- Create Genre: POST /api/genres
  * Requires JSON body: {"name": "Western"}

- Delete Genre: DELETE /api/genres/{ID}

## Project Structure

- src/main/java/.../entity: Database models (Movie, Actor, Genre).
- src/main/java/.../repository: Interfaces for database access.
- src/main/java/.../service: Business logic and rules.
- src/main/java/.../controller: API endpoints.
- src/main/resources/data: CSV files for initial data loading.

