package com.filmsociety.movies_api.repository;

import com.filmsociety.movies_api.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // --- PAGINATION METHODS (For the Controller/API) ---
    
    Page<Movie> findByReleaseYear(Integer releaseYear, Pageable pageable);

    Page<Movie> findByGenres_Id(Long genreId, Pageable pageable);

    Page<Movie> findByActors_Id(Long actorId, Pageable pageable);

    // --- STANDARD LIST METHODS (For Service Logic / Deletion Checks) ---
    // We need these because ActorService and GenreService use them to check 
    // for associated movies before deleting.
    
    List<Movie> findByGenres_Id(Long genreId);

    List<Movie> findByActors_Id(Long actorId);
    
    // --- SEARCH METHOD (Restoring the missing method) ---
    
    List<Movie> findByTitleContainingIgnoreCase(String title);
}