package com.movie.repository;

import com.movie.models.theatre.Theatre;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class InMemoryTheatreRepository implements TheatreRepository {
    private final Map<String, Theatre> theatres = new LinkedHashMap<String, Theatre>();

    @Override
    public Theatre save(Theatre theatre) {
        Objects.requireNonNull(theatre, "theatre");
        theatres.put(theatre.getId(), theatre);
        return theatre;
    }

    @Override
    public Optional<Theatre> findById(String id) { return Optional.ofNullable(theatres.get(id)); }

    @Override
    public List<Theatre> findAll() { return new ArrayList<Theatre>(theatres.values()); }

    @Override
    public boolean deleteById(String id) { return theatres.remove(id) != null; }
}
