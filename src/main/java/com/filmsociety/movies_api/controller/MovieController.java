package com.filmsociety.movies_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Genre;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.service.MovieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public Page<Movie> getAllMovies(
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long actor,
            @RequestParam(defaultValue = "0") int page,  // Default to first page
            @RequestParam(defaultValue = "10") int size  // Default to 10 items
    ) {
        
        if (genre != null) {
            return movieService.getMoviesByGenre(genre, page, size);
        } else if (year != null) {
            return movieService.getMoviesByYear(year, page, size);
        } else if (actor != null) {
            return movieService.getMoviesByActor(actor, page, size);
        }
        
        return movieService.getAllMovies(page, size);
    }
    
    
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam String title) {
        return movieService.searchMoviesByTitle(title);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/{id}/actors")
    public ResponseEntity<Set<Actor>> getActorsByMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id).getActors());
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportMoviesToCsv() {
        // 1. Get all movies (ignoring pagination for the full export)
        List<Movie> movies = movieService.getAllMovies(0, Integer.MAX_VALUE).getContent();

        // 2. Build the CSV content string
        StringBuilder csvContent = new StringBuilder();
        
        // Add Header Row
        csvContent.append("ID,Title,ReleaseYear,Duration,Genres,Actors\n");

        // Add Data Rows
        for (Movie movie : movies) {
            // Get pipe-separated lists of names for relationships
            String genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(java.util.stream.Collectors.joining("|"));

            String actorNames = movie.getActors().stream()
                    .map(Actor::getName)
                    .collect(java.util.stream.Collectors.joining("|"));

            // Append movie data
            csvContent.append(String.format("%d,\"%s\",%d,%d,\"%s\",\"%s\"\n", 
                    movie.getId(), 
                    movie.getTitle().replace("\"", "\"\""), // Handle embedded quotes
                    movie.getReleaseYear(), 
                    movie.getDuration(), 
                    genreNames, 
                    actorNames));
        }

        // 3. Set HTTP Headers for file download
        HttpHeaders headers = new HttpHeaders();
        // This tells the browser/client to download a file
        headers.setContentDispositionFormData("attachment", "movies_data.csv");
        // This tells the browser/client the file type
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        // 4. Return the content as a ResponseEntity
        return new ResponseEntity<>(csvContent.toString(), headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        return new ResponseEntity<>(movieService.createMovie(movie), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    // Inside MovieController.java

    @PatchMapping("/{movieId}/actors/add")
    public ResponseEntity<Movie> addActorsToMovie(@PathVariable Long movieId, @RequestBody Set<Actor> newActors) {
        // This endpoint expects a set of Actor objects (with IDs) and adds them to the movie's existing cast.
        Movie updatedMovie = movieService.addActorsToMovie(movieId, newActors);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}