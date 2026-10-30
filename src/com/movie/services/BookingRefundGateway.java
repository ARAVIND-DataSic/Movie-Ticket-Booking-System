package com.movie.services;

import java.util.List;

public interface BookingRefundGateway {
    BookingRefundDetails getBookingRefundDetails(int bookingId);

    List<BookingRefundDetails> getBookingRefundDetailsForShow(int showId);

    void cancelBooking(int bookingId);
}
