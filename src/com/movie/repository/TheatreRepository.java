package com.movie.repository;

import com.movie.models.theatre.Theatre;
import java.util.List;
import java.util.Optional;

public interface TheatreRepository {
    Theatre save(Theatre theatre);
    Optional<Theatre> findById(String id);
    List<Theatre> findAll();
    boolean deleteById(String id);
}
