package com.movie.services;

public interface BookingPaymentGateway {
    void confirmBooking(int bookingId);

    void markBookingFailed(int bookingId);

    void releaseLockedSeats(int bookingId);
}
