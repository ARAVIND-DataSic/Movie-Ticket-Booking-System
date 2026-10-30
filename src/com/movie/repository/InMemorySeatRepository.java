package com.movie.repository;

import com.movie.models.theatre.Seat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class InMemorySeatRepository implements SeatRepository {
    private final Map<String, Seat> seats = new LinkedHashMap<String, Seat>();

    @Override
    public Seat save(Seat seat) {
        Objects.requireNonNull(seat, "seat");
        seats.put(seat.getId(), seat);
        return seat;
    }

    @Override
    public Optional<Seat> findById(String id) { return Optional.ofNullable(seats.get(id)); }

    @Override
    public List<Seat> findAll() { return new ArrayList<Seat>(seats.values()); }

    @Override
    public List<Seat> findByScreenId(String screenId) {
        List<Seat> matches = new ArrayList<Seat>();
        for (Seat seat : seats.values()) {
            if (seat.getScreenId().equals(screenId)) matches.add(seat);
        }
        return matches;
    }

    @Override
    public boolean existsByScreenIdAndPosition(String screenId, String rowLabel, int seatNumber) {
        for (Seat seat : seats.values()) {
            if (seat.getScreenId().equals(screenId)
                    && seat.getRowLabel().equalsIgnoreCase(rowLabel)
                    && seat.getSeatNumber() == seatNumber) return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) { return seats.remove(id) != null; }
}
