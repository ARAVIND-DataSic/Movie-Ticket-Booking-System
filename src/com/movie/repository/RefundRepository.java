package com.movie.repository;

import com.movie.models.refund.Refund;
import java.util.List;
import java.util.Optional;

public interface RefundRepository {
    Refund save(Refund refund);

    Optional<Refund> findById(int refundId);

    Optional<Refund> findByBookingId(int bookingId);

    List<Refund> findAll();
}
