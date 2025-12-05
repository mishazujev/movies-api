package com.filmsociety.movies_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping; // For the Movie entity
import org.springframework.web.bind.annotation.PathVariable;                         // For the Set collection type
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.filmsociety.movies_api.entity.Actor;
import com.filmsociety.movies_api.entity.Movie;
import com.filmsociety.movies_api.service.ActorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public List<Actor> getAllActors(@RequestParam(required = false) String name) {
        if (name != null) {
            return actorService.searchActorsByName(name);
        }
        return actorService.getAllActors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    @PostMapping
    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) {
        return new ResponseEntity<>(actorService.createActor(actor), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long id, @RequestBody Actor actor) {
        return ResponseEntity.ok(actorService.updateActor(id, actor));
    }

    @PutMapping("/{actorId}/movies")
    public ResponseEntity<Actor> setMoviesForActor(@PathVariable Long actorId, @RequestBody Set<Movie> finalMovieList) {
        // Use PUT to signal that the entire resource (the movie list) is being replaced.
        Actor updatedActor = actorService.setActorMovies(actorId, finalMovieList);
        return ResponseEntity.ok(updatedActor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        actorService.deleteActor(id, force);
        return ResponseEntity.noContent().build();
    }
}