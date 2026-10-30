package com.movie.repository;

import com.movie.models.booking.Booking;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Unified BookingRepository interface.
 *
 * Exposes:
 *   - Member 1/4 methods: save(), findById(String), findByUser(), isSeatBooked()
 *   - Member 3 methods:   addBooking(), getBookingById(int), getBookingsByShowId(),
 *                         getAllBookings(), updateBooking()
 *
 * The bundled InMemory implementation satisfies both contracts.
 */
public interface BookingRepository {

    // ── Member 1/4 API ────────────────────────────────────────────────────────
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findByUser(String userId);
    boolean isSeatBooked(String showId, String seatId);

    // ── Member 3 API ──────────────────────────────────────────────────────────
    void addBooking(Booking booking);
    Booking getBookingById(int bookingId);
    List<Booking> getBookingsByShowId(int showId);
    List<Booking> getAllBookings();
    boolean updateBooking(Booking booking);

    // =========================================================================
    // Default in-memory implementation (satisfies both APIs)
    // =========================================================================
    class InMemory implements BookingRepository {

        private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

        // Member 1/4
        @Override
        public synchronized Booking save(Booking b) {
            bookings.put(b.getId(), b);
            return b;
        }

        @Override
        public Optional<Booking> findById(String id) {
            return Optional.ofNullable(bookings.get(id));
        }

        @Override
        public List<Booking> findByUser(String userId) {
            return bookings.values().stream()
                    .filter(b -> userId.equals(b.getStringUserId()) || userId.equals(b.getUserId()))
                    .toList();
        }

        @Override
        public boolean isSeatBooked(String showId, String seatId) {
            return bookings.values().stream()
                    .anyMatch(b -> b.getBookingStatus() == com.movie.models.booking.BookingStatus.CONFIRMED
                            && showId.equals(b.getStringShowId())
                            && b.getSeatIds().contains(seatId));
        }

        // Member 3
        @Override
        public synchronized void addBooking(Booking booking) {
            if (booking == null) throw new IllegalArgumentException("Booking cannot be null");
            bookings.put(booking.getId(), booking);
        }

        @Override
        public Booking getBookingById(int bookingId) {
            return bookings.values().stream()
                    .filter(b -> b.getBookingId() == bookingId)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Booking> getBookingsByShowId(int showId) {
            return bookings.values().stream()
                    .filter(b -> b.getShowId() == showId)
                    .toList();
        }

        @Override
        public List<Booking> getAllBookings() {
            return List.copyOf(bookings.values());
        }

        @Override
        public synchronized boolean updateBooking(Booking booking) {
            if (booking == null) return false;
            if (bookings.containsKey(booking.getId())) {
                bookings.put(booking.getId(), booking);
                return true;
            }
            return false;
        }
    }
}
