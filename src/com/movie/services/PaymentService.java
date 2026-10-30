package com.movie.services;

import com.movie.models.payment.Payment;
import com.movie.models.payment.PaymentMethod;
import com.movie.models.payment.PaymentStatus;
import com.movie.repository.PaymentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaymentService {
    private static final BigDecimal CONVENIENCE_FEE_PER_TICKET = new BigDecimal("20.00");
    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    private final PaymentRepository paymentRepository;
    private final BookingPaymentGateway bookingGateway;
    private final OfferService offerService;

    public PaymentService(PaymentRepository paymentRepository, BookingPaymentGateway bookingGateway,
            OfferService offerService) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "Payment repository is required.");
        this.bookingGateway = Objects.requireNonNull(bookingGateway, "Booking gateway is required.");
        this.offerService = Objects.requireNonNull(offerService, "Offer service is required.");
    }

    public BigDecimal calculateTotalAmount(List<BigDecimal> seatPrices, BigDecimal offerDiscount) {
        if (seatPrices == null || seatPrices.isEmpty()) {
            throw new IllegalArgumentException("At least one seat price is required.");
        }
        if (offerDiscount == null || offerDiscount.signum() < 0) {
            throw new IllegalArgumentException("Offer discount cannot be negative or null.");
        }

        BigDecimal seatTotal = BigDecimal.ZERO;
        for (BigDecimal seatPrice : seatPrices) {
            if (seatPrice == null || seatPrice.signum() < 0) {
                throw new IllegalArgumentException("Seat prices cannot be negative or null.");
            }
            seatTotal = seatTotal.add(seatPrice);
        }

        BigDecimal convenienceFee = CONVENIENCE_FEE_PER_TICKET.multiply(BigDecimal.valueOf(seatPrices.size()));
        BigDecimal convenienceFeeGst = convenienceFee.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        if (offerDiscount.compareTo(seatTotal) > 0) {
            throw new IllegalArgumentException("Offer discount cannot exceed the sum of seat prices.");
        }
        return seatTotal.add(convenienceFee).add(convenienceFeeGst).subtract(offerDiscount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Payment createPayment(int bookingId, int userId, BigDecimal amount, PaymentMethod method) {
        return createPayment(bookingId, userId, amount, method, LocalDateTime.now());
    }

    public Payment createPayment(int bookingId, int userId, BigDecimal amount,
            PaymentMethod method, LocalDateTime createdAt) {
        Payment payment = new Payment(bookingId, userId, amount, createdAt, method);
        return paymentRepository.save(payment);
    }

    public Payment processPayment(int paymentId, com.movie.payment.PaymentMethod processor) {
        return processPayment(paymentId, processor, LocalDateTime.now());
    }

    public Payment processPayment(int paymentId, com.movie.payment.PaymentMethod processor,
            LocalDateTime processedAt) {
        Objects.requireNonNull(processor, "Payment processor is required.");
        Objects.requireNonNull(processedAt, "Payment processing date is required.");
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payments can be processed.");
        }
        if (processor.getPaymentMethod() != payment.getMethod()) {
            throw new IllegalArgumentException("Payment processor does not match the recorded payment method.");
        }

        boolean successful = processor.processPayment(payment.getAmount());
        payment.setStatus(successful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        paymentRepository.save(payment);
        if (successful) {
            bookingGateway.confirmBooking(payment.getBookingId());
            offerService.completeBookingOffer(payment.getBookingId(), payment.getUserId(), processedAt);
        } else {
            bookingGateway.markBookingFailed(payment.getBookingId());
            bookingGateway.releaseLockedSeats(payment.getBookingId());
            offerService.releaseBookingOffer(payment.getBookingId());
        }
        return payment;
    }

    public Optional<Payment> findPayment(int paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentHistory(int bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void markRefunded(int paymentId, BigDecimal refundAmount) {
        if (refundAmount == null || refundAmount.signum() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive.");
        }
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Only successful payments can be refunded.");
        }
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed the payment amount.");
        }
        payment.setStatus(refundAmount.compareTo(payment.getAmount()) == 0
                ? PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
        paymentRepository.save(payment);
    }
}
