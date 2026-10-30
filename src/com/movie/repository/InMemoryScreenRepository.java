package com.movie.repository;

import com.movie.models.theatre.Screen;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class InMemoryScreenRepository implements ScreenRepository {
    private final Map<String, Screen> screens = new LinkedHashMap<String, Screen>();

    @Override
    public Screen save(Screen screen) {
        Objects.requireNonNull(screen, "screen");
        screens.put(screen.getId(), screen);
        return screen;
    }

    @Override
    public Optional<Screen> findById(String id) { return Optional.ofNullable(screens.get(id)); }

    @Override
    public List<Screen> findAll() { return new ArrayList<Screen>(screens.values()); }

    @Override
    public List<Screen> findByTheatreId(String theatreId) {
        List<Screen> matches = new ArrayList<Screen>();
        for (Screen screen : screens.values()) {
            if (screen.getTheatreId().equals(theatreId)) matches.add(screen);
        }
        return matches;
    }

    @Override
    public boolean deleteById(String id) { return screens.remove(id) != null; }
}
