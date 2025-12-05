package com.filmsociety.movies_api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.exception.BadRequestException;
import com.filmsociety.movies_api.exception.ResourceNotFoundException;  // For the Movie entity
import com.filmsociety.movies_api.repository.ActorRepository;        // For the Set collection type
import com.filmsociety.movies_api.repository.MovieRepository;         // For the HashSet implementation

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Actor getActorById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
    }

    public List<Actor> searchActorsByName(String name) {
        return actorRepository.findByNameContainingIgnoreCase(name);
    }

    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Actor updateActor(Long id, Actor actorDetails) {
        Actor actor = getActorById(id);
        if (actorDetails.getName() != null) {
            actor.setName(actorDetails.getName());
        }
        if (actorDetails.getBirthDate() != null) {
            actor.setBirthDate(actorDetails.getBirthDate());
        }
    
        // Update primitive fields
        if (actorDetails.getName() != null) {
            actor.setName(actorDetails.getName());
        }
        if (actorDetails.getBirthDate() != null) {
            actor.setBirthDate(actorDetails.getBirthDate());
        
        }


        // --- ADD LOGIC TO HANDLE MOVIES RELATIONSHIP ---
        if (actorDetails.getMovies() != null) {
            // This line replaces the actor's entire filmography with the set provided in the JSON.
            actor.setMovies(actorDetails.getMovies());
        }
        // ------------------------------------------------

        return actorRepository.save(actor);
    }

    @Transactional
    public Actor setActorMovies(Long actorId, Set<Movie> finalMovieList) {
        // 1. Retrieve the existing Actor.
        Actor actor = getActorById(actorId);

        // 2. Look up the Movie IDs provided in the list and convert them to managed entities.
        Set<Movie> moviesToSet = new HashSet<>();
        for (Movie movie : finalMovieList) {
            if (movie.getId() != null) {
                // Find the persistent Movie entity
                Movie foundMovie = movieRepository.findById(movie.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movie.getId()));
                moviesToSet.add(foundMovie);
            }
        }
        
        // 3. Simple replacement: Overwrite the old list entirely.
        actor.setMovies(moviesToSet);
        
        // 4. Save and return the updated actor.
        return actorRepository.save(actor);
    }

    @Transactional
    public void deleteActor(Long id, boolean force) {
        Actor actor = getActorById(id);
        List<Movie> movies = movieRepository.findByActors_Id(id);

        if (!movies.isEmpty()) {
            if (!force) {
                throw new BadRequestException("Unable to delete actor '" + actor.getName() + "' as they are associated with " + movies.size() + " movies");
            }
            for (Movie movie : movies) {
                movie.getActors().remove(actor);
                movieRepository.save(movie);
            }
        }
        actorRepository.delete(actor);
    }
}