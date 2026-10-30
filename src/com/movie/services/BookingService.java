package com.movie.services;
import com.movie.models.booking.Booking;
import com.movie.models.users.User;
import com.movie.repository.*;
import java.util.*;
public class BookingService {
    private final UserRepository users;
    private final BookingRepository bookings;
    private final SeatLockService seatLocks;
    public BookingService(UserRepository users,BookingRepository bookings,SeatLockService seatLocks) { this.users=users; this.bookings=bookings; this.seatLocks=seatLocks; }
    public List<com.movie.models.booking.SeatLock> lockSeats(String userId,String showId,List<String> seatIds) {
        requireUser(userId); return seatLocks.lockSeats(userId,showId,seatIds);
    }
    public synchronized Booking confirmBooking(String userId,String showId,List<String> seatIds) {
        requireUser(userId);
        seatLocks.consume(userId,showId,seatIds);
        return bookings.save(new Booking(userId,showId,seatIds));
    }
    public List<Booking> getBookings(String userId) { requireUser(userId); return bookings.findByUser(userId); }
    private User requireUser(String id) { return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")); }
}
