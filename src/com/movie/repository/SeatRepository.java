package com.movie.repository;

import com.movie.models.theatre.Seat;
import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Seat save(Seat seat);
    Optional<Seat> findById(String id);
    List<Seat> findAll();
    List<Seat> findByScreenId(String screenId);
    boolean existsByScreenIdAndPosition(String screenId, String rowLabel, int seatNumber);
    boolean deleteById(String id);
}
