package com.movie.models.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private int id;
    private final int bookingId;
    private final int userId;
    private final BigDecimal amount;
    private final LocalDateTime paymentDate;
    private final PaymentMethod method;
    private PaymentStatus status;

    public Payment(int bookingId, int userId, BigDecimal amount, LocalDateTime paymentDate,
            PaymentMethod method) {
        if (bookingId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Booking ID and user ID must be positive.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        this.paymentDate = Objects.requireNonNull(paymentDate, "Payment date is required.");
        this.method = Objects.requireNonNull(method, "Payment method is required.");
        this.status = PaymentStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Payment ID must be positive.");
        }
        this.id = id;
    }

    public void setStatus(PaymentStatus status) {
        this.status = Objects.requireNonNull(status, "Payment status is required.");
    }
}
