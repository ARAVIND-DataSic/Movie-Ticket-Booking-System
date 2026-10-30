package com.movie.models.refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Refund {
    private int id;
    private final int bookingId;
    private final int paymentId;
    private final BigDecimal amount;
    private final LocalDateTime refundDate;
    private final String reason;

    public Refund(int bookingId, int paymentId, BigDecimal amount, LocalDateTime refundDate, String reason) {
        if (bookingId <= 0 || paymentId <= 0) {
            throw new IllegalArgumentException("Booking ID and payment ID must be positive.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund reason is required.");
        }
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        this.refundDate = Objects.requireNonNull(refundDate, "Refund date is required.");
        this.reason = reason.trim();
    }

    public int getId() {
        return id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public String getReason() {
        return reason;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Refund ID must be positive.");
        }
        this.id = id;
    }
}
