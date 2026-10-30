package com.movie.services;

import com.movie.models.movie.Movie;
import com.movie.repository.MovieRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = Objects.requireNonNull(movieRepository, "movieRepository");
    }

    public Movie save(Movie movie) {
        Objects.requireNonNull(movie, "movie");
        return movieRepository.save(movie);
    }

    public Optional<Movie> findById(String id) { return movieRepository.findById(requireId(id)); }
    public List<Movie> findAll() { return movieRepository.findAll(); }

    public void delete(String id) {
        if (!movieRepository.deleteById(requireId(id))) {
            throw new IllegalArgumentException("Movie '" + id + "' was not found.");
        }
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id must not be blank.");
        return id.trim();
    }
}
