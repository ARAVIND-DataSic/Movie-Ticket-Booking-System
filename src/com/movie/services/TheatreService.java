package com.movie.services;

import com.movie.models.theatre.Theatre;
import com.movie.repository.ScreenRepository;
import com.movie.repository.TheatreRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TheatreService {
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;

    public TheatreService(TheatreRepository theatreRepository, ScreenRepository screenRepository) {
        this.theatreRepository = Objects.requireNonNull(theatreRepository, "theatreRepository");
        this.screenRepository = Objects.requireNonNull(screenRepository, "screenRepository");
    }

    public Theatre save(Theatre theatre) {
        Objects.requireNonNull(theatre, "theatre");
        return theatreRepository.save(theatre);
    }

    public Optional<Theatre> findById(String id) { return theatreRepository.findById(requireId(id)); }
    public List<Theatre> findAll() { return theatreRepository.findAll(); }

    public void delete(String id) {
        String validId = requireId(id);
        if (!theatreRepository.findById(validId).isPresent()) {
            throw new IllegalArgumentException("Theatre '" + validId + "' was not found.");
        }
        if (!screenRepository.findByTheatreId(validId).isEmpty()) {
            throw new IllegalStateException("Delete the theatre's screens before deleting the theatre.");
        }
        theatreRepository.deleteById(validId);
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id must not be blank.");
        return id.trim();
    }
}
