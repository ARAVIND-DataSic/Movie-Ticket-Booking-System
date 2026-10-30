package com.movie.repository;

import java.util.List;
import com.movie.models.booking.Booking;

public interface BookingRepository {
    void addBooking(Booking booking);
    Booking getBookingById(int bookingId);
    List<Booking> getBookingsByShowId(int showId);
    List<Booking> getAllBookings();
    boolean updateBooking(Booking booking);
}
