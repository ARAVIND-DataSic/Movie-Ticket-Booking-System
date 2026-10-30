package com.movie.repository;

import com.movie.models.refund.Refund;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRefundRepository implements RefundRepository {
    private final Map<Integer, Refund> refunds = new HashMap<>();
    private final Map<Integer, Integer> refundIdsByBooking = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public synchronized Refund save(Refund refund) {
        if (refund == null) {
            throw new IllegalArgumentException("Refund is required.");
        }
        if (refundIdsByBooking.containsKey(refund.getBookingId())) {
            throw new IllegalStateException("A refund already exists for this booking.");
        }
        refund.setId(nextId.getAndIncrement());
        refunds.put(refund.getId(), refund);
        refundIdsByBooking.put(refund.getBookingId(), refund.getId());
        return refund;
    }

    @Override
    public synchronized Optional<Refund> findById(int refundId) {
        return Optional.ofNullable(refunds.get(refundId));
    }

    @Override
    public synchronized Optional<Refund> findByBookingId(int bookingId) {
        Integer refundId = refundIdsByBooking.get(bookingId);
        return refundId == null ? Optional.empty() : findById(refundId);
    }

    @Override
    public synchronized List<Refund> findAll() {
        return new ArrayList<>(refunds.values());
    }
}
