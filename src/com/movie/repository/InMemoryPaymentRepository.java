package com.movie.repository;

import com.movie.models.payment.Payment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<Integer, Payment> payments = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public synchronized Payment save(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment is required.");
        }
        if (payment.getId() == 0) {
            payment.setId(nextId.getAndIncrement());
        } else {
            nextId.updateAndGet(current -> Math.max(current, payment.getId() + 1));
        }
        payments.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public synchronized Optional<Payment> findById(int paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public synchronized List<Payment> findByBookingId(int bookingId) {
        List<Payment> history = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.getBookingId() == bookingId) {
                history.add(payment);
            }
        }
        history.sort(Comparator.comparing(Payment::getPaymentDate).thenComparingInt(Payment::getId));
        return history;
    }

    @Override
    public synchronized List<Payment> findAll() {
        List<Payment> allPayments = new ArrayList<>(payments.values());
        allPayments.sort(Comparator.comparingInt(Payment::getId));
        return allPayments;
    }
}
