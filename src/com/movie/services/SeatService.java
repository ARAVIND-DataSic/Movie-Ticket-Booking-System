package com.movie.services;

import com.movie.models.theatre.Seat;
import com.movie.models.theatre.Screen;
import com.movie.repository.ScreenRepository;
import com.movie.repository.SeatRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SeatService {
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;

    public SeatService(SeatRepository seatRepository, ScreenRepository screenRepository) {
        this.seatRepository = Objects.requireNonNull(seatRepository, "seatRepository");
        this.screenRepository = Objects.requireNonNull(screenRepository, "screenRepository");
    }

    public Seat save(Seat seat) {
        Objects.requireNonNull(seat, "seat");
        Screen screen = screenRepository.findById(seat.getScreenId())
                .orElseThrow(() -> new IllegalArgumentException("Screen '" + seat.getScreenId() + "' was not found."));
        Optional<Seat> existing = seatRepository.findById(seat.getId());
        List<Seat> seatsOnScreen = seatRepository.findByScreenId(seat.getScreenId());
        int seatsOnTargetScreen = seatsOnScreen.size();
        for (Seat current : seatsOnScreen) {
            if (existing.isPresent() && current.getId().equals(seat.getId())) {
                seatsOnTargetScreen--;
                continue;
            }
            if (current.getRowLabel().equalsIgnoreCase(seat.getRowLabel())
                    && current.getSeatNumber() == seat.getSeatNumber()) {
                throw new IllegalArgumentException("That row and seat number already exist on this screen.");
            }
        }
        if (seatsOnTargetScreen >= screen.getSeatCapacity()) {
            throw new IllegalStateException("The screen has reached its seat capacity.");
        }
        return seatRepository.save(seat);
    }

    public Optional<Seat> findById(String id) { return seatRepository.findById(requireId(id)); }
    public List<Seat> findAll() { return seatRepository.findAll(); }
    public List<Seat> findByScreenId(String screenId) {
        return seatRepository.findByScreenId(requireId(screenId));
    }

    public void delete(String id) {
        if (!seatRepository.deleteById(requireId(id))) {
            throw new IllegalArgumentException("Seat '" + id + "' was not found.");
        }
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("id must not be blank.");
        return id.trim();
    }
}
