package com.movie.services;

import java.util.ArrayList;
import java.util.List;

import com.movie.models.booking.Booking;
import com.movie.models.show.Show;
import com.movie.repository.BookingRepository;

public class BookingService {

    private List<Booking> inMemoryBookings = new ArrayList<>();
    private BookingRepository bookingRepository;

    public BookingService() {
    }

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

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
