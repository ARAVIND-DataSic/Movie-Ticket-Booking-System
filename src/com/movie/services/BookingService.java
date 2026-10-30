package com.movie.services;

import java.util.ArrayList;
import java.util.List;

import com.movie.models.booking.Booking;
import com.movie.models.show.Show;
import com.movie.models.users.User;
import com.movie.repository.BookingRepository;
import com.movie.repository.SeatLockRepository;
import com.movie.repository.UserRepository;

/**
 * Unified BookingService.
 *
 * Preserves both:
 *   - Member 1/4 flow: lockSeats() → confirmBooking() via UserRepository,
 *     BookingRepository (Member 1 API), and SeatLockService.
 *   - Member 3 flow:   createBooking(int, Show, ...) with in-memory list,
 *     show booking counter, and show-level cancellation coordination.
 */
public class BookingService {

    // ── Member 1/4 dependencies ───────────────────────────────────────────────
    private UserRepository users;
    private SeatLockService seatLocks;

    // ── Member 3 dependency ───────────────────────────────────────────────────
    private List<Booking> inMemoryBookings = new ArrayList<>();
    private BookingRepository bookingRepository;

    // =========================================================================
    // Member 1/4 constructor
    // =========================================================================
    public BookingService(UserRepository users, BookingRepository bookings, SeatLockService seatLocks) {
        this.users              = users;
        this.bookingRepository  = bookings;
        this.seatLocks          = seatLocks;
    }

    // =========================================================================
    // Member 3 constructors
    // =========================================================================
    public BookingService() {
    }

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // =========================================================================
    // Member 1/4 operations
    // =========================================================================

    public List<com.movie.models.booking.SeatLock> lockSeats(
            String userId, String showId, List<String> seatIds) {
        requireUser(userId);
        return seatLocks.lockSeats(userId, showId, seatIds);
    }

    public synchronized Booking confirmBooking(
            String userId, String showId, List<String> seatIds) {
        requireUser(userId);
        seatLocks.consume(userId, showId, seatIds);
        Booking booking = new Booking(userId, showId, seatIds);
        if (bookingRepository != null) {
            bookingRepository.save(booking);
        }
        inMemoryBookings.add(booking);
        return booking;
    }

    public List<Booking> getBookings(String userId) {
        requireUser(userId);
        if (bookingRepository != null) {
            return bookingRepository.findByUser(userId);
        }
        List<Booking> result = new ArrayList<>();
        for (Booking b : inMemoryBookings) {
            if (userId.equals(b.getUserId()) || userId.equals(b.getStringUserId())) {
                result.add(b);
            }
        }
        return result;
    }

    private User requireUser(String id) {
        if (users == null) throw new IllegalStateException("UserRepository not configured");
        return users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // =========================================================================
    // Member 3 operations
    // =========================================================================

    /**
     * Creates a single-seat booking for the given show.
     * Ticket price is derived from the show's current pricing at the moment of booking
     * (ensuring updated prices apply only to future bookings).
     */
    public Booking createBooking(int bookingId, Show show, String userId,
                                 String seatNumber, String seatType,
                                 double convenienceFee) {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }
        if ("CANCELLED".equalsIgnoreCase(show.getStatus())) {
            throw new IllegalArgumentException("Cannot book seats for a cancelled show");
        }

        double price;
        if ("PREMIUM".equalsIgnoreCase(seatType)) {
            price = show.getPremiumPrice();
        } else if ("RECLINER".equalsIgnoreCase(seatType)) {
            price = show.getReclinerPrice();
        } else {
            price = show.getRegularPrice();
        }

        Booking booking = new Booking(
                bookingId,
                show.getShowId(),
                userId,
                seatNumber,
                seatType,
                price,
                convenienceFee
        );

        addBooking(booking);
        show.addBooking();
        return booking;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        inMemoryBookings.add(booking);
        if (bookingRepository != null) {
            bookingRepository.addBooking(booking);
        }
    }

    public boolean hasBookingsForShow(int showId) {
        for (Booking b : getAllBookings()) {
            if (b.getShowId() == showId && !"CANCELLED".equalsIgnoreCase(b.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public List<Booking> getBookingsByShowId(int showId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : getAllBookings()) {
            if (b.getShowId() == showId) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Booking> getAllBookings() {
        if (bookingRepository != null) {
            List<Booking> repoBookings = bookingRepository.getAllBookings();
            if (repoBookings != null && !repoBookings.isEmpty()) {
                return repoBookings;
            }
        }
        return inMemoryBookings;
    }

    public void cancelBookingsForShow(int showId) {
        for (Booking b : getBookingsByShowId(showId)) {
            b.setStatus("CANCELLED");
        }
    }
}
