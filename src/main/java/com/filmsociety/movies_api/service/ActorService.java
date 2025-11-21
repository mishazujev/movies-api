package com.filmsociety.movies_api.service;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.exception.BadRequestException;
import com.filmsociety.movies_api.exception.ResourceNotFoundException;
import com.filmsociety.movies_api.repository.ActorRepository;
import com.filmsociety.movies_api.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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