package com.filmsociety.movies_api.repository;

import com.filmsociety.movies_api.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    // This method allows searching for actors by name (case insensitive)
    List<Actor> findByNameContainingIgnoreCase(String name);
}