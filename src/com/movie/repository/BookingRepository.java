package com.movie.repository;
import com.movie.models.booking.Booking;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findByUser(String userId);
    boolean isSeatBooked(String showId, String seatId);
    class InMemory implements BookingRepository {
        private final Map<String,Booking> bookings = new ConcurrentHashMap<>();
        public synchronized Booking save(Booking b) { bookings.put(b.getId(),b); return b; }
        public Optional<Booking> findById(String id) { return Optional.ofNullable(bookings.get(id)); }
        public List<Booking> findByUser(String userId) { return bookings.values().stream().filter(b -> b.getUserId().equals(userId)).toList(); }
        public boolean isSeatBooked(String showId,String seatId) { return bookings.values().stream().anyMatch(b -> b.getStatus()==com.movie.models.booking.BookingStatus.CONFIRMED && b.getShowId().equals(showId) && b.getSeatIds().contains(seatId)); }
    }
}
