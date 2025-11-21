package com.filmsociety.movies_api.bootstrap;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Genre;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.repository.ActorRepository;
import com.filmsociety.movies_api.repository.GenreRepository;
import com.filmsociety.movies_api.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    // We need these repositories to save data to the database
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    // We use these Maps to "remember" the objects we just saved.
    // Key = The Name (String), Value = The Database Object (Entity)
    private final Map<String, Genre> genreMap = new HashMap<>();
    private final Map<String, Actor> actorMap = new HashMap<>();

    public DataLoader(GenreRepository genreRepository, ActorRepository actorRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // If the database is empty, load our CSV files
        if (genreRepository.count() == 0) {
            System.out.println("Starting Data Loading...");
            
            loadGenres();
            loadActors();
            loadMovies();
            
            System.out.println("...Data Loading Finished Successfully.");
        }
    }

    // STEP 1: Read genres.csv
    private void loadGenres() throws IOException {
        // Access the file in the resources folder
        InputStream inputStream = getClass().getResourceAsStream("/data/genres.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        // Loop through every line in the file
        while ((line = reader.readLine()) != null) {
            String genreName = line.trim();
            if (!genreName.isEmpty()) {
                // 1. Create the object
                Genre genre = new Genre(genreName);
                // 2. Save to Database
                Genre savedGenre = genreRepository.save(genre);
                // 3. Put in Map so we can find it later by name
                genreMap.put(genreName, savedGenre);
            }
        }
        reader.close();
    }

    // STEP 2: Read actors.csv
    private void loadActors() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/data/actors.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Line format: Leonardo DiCaprio,1974-11-11
            String[] parts = line.split(",");
            
            String name = parts[0].trim();
            String dateString = parts[1].trim();

            // Convert String date to LocalDate object
            LocalDate birthDate = LocalDate.parse(dateString);

            Actor actor = new Actor(name, birthDate);
            Actor savedActor = actorRepository.save(actor);
            
            // Add to map so we can look it up later
            actorMap.put(name, savedActor);
        }
        reader.close();
    }

    // STEP 3: Read movies.csv
    private void loadMovies() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/data/movies.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Line format: Inception,2010,148,Action|Sci-Fi,Leonardo DiCaprio
            String[] parts = line.split(",");

            String title = parts[0].trim();
            // We must convert Strings to Integers for numbers
            int year = Integer.parseInt(parts[1].trim());
            int duration = Integer.parseInt(parts[2].trim());

            // Create the movie object
            Movie movie = new Movie(title, year, duration);

            // HANDLE GENRES: Split "Action|Sci-Fi" by the pipe symbol
            String[] genreNames = parts[3].split("\\|");
            for (String name : genreNames) {
                // Use the Map to find the real Genre object we saved in Step 1
                Genre foundGenre = genreMap.get(name.trim());
                if (foundGenre != null) {
                    movie.getGenres().add(foundGenre);
                }
            }

            // HANDLE ACTORS: Split "Leonardo DiCaprio|Tom Hardy"
            String[] actorNames = parts[4].split("\\|");
            for (String name : actorNames) {
                // Use the Map to find the real Actor object we saved in Step 2
                Actor foundActor = actorMap.get(name.trim());
                if (foundActor != null) {
                    movie.getActors().add(foundActor);
                }
            }

            movieRepository.save(movie);
        }
        reader.close();
    }
}