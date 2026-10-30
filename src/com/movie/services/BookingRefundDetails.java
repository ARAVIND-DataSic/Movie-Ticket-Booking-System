package com.movie.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingRefundDetails {
    private final int bookingId;
    private final int paymentId;
    private final LocalDateTime showtime;
    private final BigDecimal ticketAmount;
    private final BigDecimal totalAmount;
    private final boolean cancellable;

    public BookingRefundDetails(int bookingId, int paymentId, LocalDateTime showtime,
            BigDecimal ticketAmount, BigDecimal totalAmount, boolean cancellable) {
        if (bookingId <= 0 || paymentId <= 0) {
            throw new IllegalArgumentException("Booking ID and payment ID must be positive.");
        }
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.showtime = Objects.requireNonNull(showtime, "Showtime is required.");
        this.ticketAmount = requireNonNegative(ticketAmount, "Ticket amount");
        this.totalAmount = requireNonNegative(totalAmount, "Total booking amount");
        if (this.ticketAmount.compareTo(this.totalAmount) > 0) {
            throw new IllegalArgumentException("Ticket amount cannot exceed the total booking amount.");
        }
        this.cancellable = cancellable;
    }

    private static BigDecimal requireNonNegative(BigDecimal amount, String fieldName) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative or null.");
        }
        return amount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public BigDecimal getTicketAmount() {
        return ticketAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isCancellable() {
        return cancellable;
    }
}
