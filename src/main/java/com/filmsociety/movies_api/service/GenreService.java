package com.filmsociety.movies_api.service;

import com.filmsociety.movies_api.entity.Genre;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.exception.BadRequestException;
import com.filmsociety.movies_api.exception.ResourceNotFoundException;
import com.filmsociety.movies_api.repository.GenreRepository;
import com.filmsociety.movies_api.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
    }

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre updateGenre(Long id, Genre genreDetails) {
        Genre genre = getGenreById(id);
        // PATCH: Only update if the name is provided
        if (genreDetails.getName() != null) {
            genre.setName(genreDetails.getName());
        }
        return genreRepository.save(genre);
    }

    @Transactional
    public void deleteGenre(Long id, boolean force) {
        Genre genre = getGenreById(id);
        List<Movie> movies = movieRepository.findByGenres_Id(id);

        if (!movies.isEmpty()) {
            if (!force) {
                throw new BadRequestException("Cannot delete genre '" + genre.getName() + "' because it has " + movies.size() + " associated movies.");
            }
            // If force is true, remove the relationship from movies first
            for (Movie movie : movies) {
                movie.getGenres().remove(genre);
                movieRepository.save(movie);
            }
        }
        genreRepository.delete(genre);
    }
}