package com.movie.repository;

import com.movie.models.theatre.Screen;
import java.util.List;
import java.util.Optional;

public interface ScreenRepository {
    Screen save(Screen screen);
    Optional<Screen> findById(String id);
    List<Screen> findAll();
    List<Screen> findByTheatreId(String theatreId);
    boolean deleteById(String id);
}
