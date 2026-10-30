package com.movie.repository;

import com.movie.models.movie.Movie;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class InMemoryMovieRepository implements MovieRepository {
    private final Map<String, Movie> movies = new LinkedHashMap<String, Movie>();

    @Override
    public Movie save(Movie movie) {
        Objects.requireNonNull(movie, "movie");
        movies.put(movie.getId(), movie);
        return movie;
    }

    @Override
    public Optional<Movie> findById(String id) { return Optional.ofNullable(movies.get(id)); }

    @Override
    public List<Movie> findAll() { return new ArrayList<Movie>(movies.values()); }

    @Override
    public boolean deleteById(String id) { return movies.remove(id) != null; }
}
