package com.movie.repository;

import com.movie.models.payment.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(int paymentId);

    List<Payment> findByBookingId(int bookingId);

    List<Payment> findAll();
}
