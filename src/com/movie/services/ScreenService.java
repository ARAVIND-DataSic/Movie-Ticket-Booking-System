package com.movie.services;

import com.movie.models.theatre.Screen;
import com.movie.repository.ScreenRepository;
import com.movie.repository.SeatRepository;
import com.movie.repository.TheatreRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ScreenService {
    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final SeatRepository seatRepository;

    public ScreenService(ScreenRepository screenRepository, TheatreRepository theatreRepository,
                         SeatRepository seatRepository) {
        this.screenRepository = Objects.requireNonNull(screenRepository, "screenRepository");
        this.theatreRepository = Objects.requireNonNull(theatreRepository, "theatreRepository");
        this.seatRepository = Objects.requireNonNull(seatRepository, "seatRepository");
    }

    public Screen save(Screen screen) {
        Objects.requireNonNull(screen, "screen");
        if (!theatreRepository.findById(screen.getTheatreId()).isPresent()) {
            throw new IllegalArgumentException("Theatre '" + screen.getTheatreId() + "' was not found.");
        }
        Optional<Screen> existing = screenRepository.findById(screen.getId());
        if (existing.isPresent()) {
            Screen old = existing.get();
            if (screen.getSeatCapacity() < seatRepository.findByScreenId(screen.getId()).size()) {
                throw new IllegalStateException("Seat capacity cannot be less than the number of seats already configured.");
            }
            if (!old.getTheatreId().equals(screen.getTheatreId())
                    && !seatRepository.findByScreenId(screen.getId()).isEmpty()) {
                throw new IllegalStateException("A screen with seats cannot be moved to another theatre.");
            }
        }
        return screenRepository.save(screen);
    }

    public Optional<Screen> findById(String id) { return screenRepository.findById(requireId(id)); }
    public List<Screen> findAll() { return screenRepository.findAll(); }
    public List<Screen> findByTheatreId(String theatreId) {
        return screenRepository.findByTheatreId(requireId(theatreId));
    }

    public void delete(String id) {
        String validId = requireId(id);
        if (!screenRepository.findById(validId).isPresent()) {
            throw new IllegalArgumentException("Screen '" + validId + "' was not found.");
        }
        if (!seatRepository.findByScreenId(validId).isEmpty()) {
            throw new IllegalStateException("Delete the screen's seats before deleting the screen.");
        }
        screenRepository.deleteById(validId);
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id must not be blank.");
        return id.trim();
    }
}
