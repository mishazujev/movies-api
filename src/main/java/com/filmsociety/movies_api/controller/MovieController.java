package com.filmsociety.movies_api.controller;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

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

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        return new ResponseEntity<>(movieService.createMovie(movie), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}