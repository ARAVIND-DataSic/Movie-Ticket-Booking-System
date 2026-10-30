package com.movie.repository;

import com.movie.models.movie.Movie;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    Movie save(Movie movie);
    Optional<Movie> findById(String id);
    List<Movie> findAll();
    boolean deleteById(String id);
}
