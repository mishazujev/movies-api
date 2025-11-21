package com.filmsociety.movies_api.service;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Genre;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.exception.ResourceNotFoundException;
import com.filmsociety.movies_api.repository.ActorRepository;
import com.filmsociety.movies_api.repository.GenreRepository;
import com.filmsociety.movies_api.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
    }

    public Page<Movie> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    public Page<Movie> getMoviesByGenre(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByGenres_Id(genreId, pageable);
    }

    public Page<Movie> getMoviesByYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByReleaseYear(year, pageable);
    }

    public Page<Movie> getMoviesByActor(Long actorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByActors_Id(actorId, pageable);
    }
    
    // Bonus: Search by title
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);

        if (movieDetails.getTitle() != null) movie.setTitle(movieDetails.getTitle());
        if (movieDetails.getReleaseYear() != null) movie.setReleaseYear(movieDetails.getReleaseYear());
        if (movieDetails.getDuration() != null) movie.setDuration(movieDetails.getDuration());
        
        // Update Relationships if provided
        if (movieDetails.getGenres() != null && !movieDetails.getGenres().isEmpty()) {
            // In a real app, you'd fetch these from DB to ensure they exist
            movie.setGenres(movieDetails.getGenres()); 
        }
        if (movieDetails.getActors() != null && !movieDetails.getActors().isEmpty()) {
            movie.setActors(movieDetails.getActors());
        }

        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }
}