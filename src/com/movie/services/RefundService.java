package com.movie.services;

import com.movie.models.payment.Payment;
import com.movie.models.payment.PaymentStatus;
import com.movie.models.refund.Refund;
import com.movie.repository.RefundRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RefundService {
    private static final String CUSTOMER_CANCELLATION_REASON = "Customer cancellation";
    private static final String SHOW_CANCELLATION_REASON = "Show cancelled by administrator";

    private final PaymentService paymentService;
    private final BookingRefundGateway bookingGateway;
    private final RefundRepository refundRepository;

    public RefundService(PaymentService paymentService, BookingRefundGateway bookingGateway,
            RefundRepository refundRepository) {
        this.paymentService = Objects.requireNonNull(paymentService, "Payment service is required.");
        this.bookingGateway = Objects.requireNonNull(bookingGateway, "Booking gateway is required.");
        this.refundRepository = Objects.requireNonNull(refundRepository, "Refund repository is required.");
    }

    public Refund cancelBookingByCustomer(int bookingId, LocalDateTime cancelledAt) {
        Objects.requireNonNull(cancelledAt, "Cancellation date is required.");
        BookingRefundDetails details = bookingGateway.getBookingRefundDetails(bookingId);
        if (details == null || details.getBookingId() != bookingId) {
            throw new IllegalArgumentException("Booking details not found: " + bookingId);
        }
        if (!details.isCancellable()) {
            throw new IllegalStateException("Booking is not active and cannot be cancelled.");
        }
        LocalDateTime cancellationDeadline = details.getShowtime().minusHours(4);
        if (cancelledAt.isAfter(cancellationDeadline)) {
            throw new IllegalStateException("Booking cancellation is not allowed within four hours of showtime.");
        }

        validateRefund(details, details.getTicketAmount());
        bookingGateway.cancelBooking(bookingId);
        return createRefund(details, details.getTicketAmount(), CUSTOMER_CANCELLATION_REASON, cancelledAt);
    }

    public List<Refund> cancelShowByAdmin(int showId, LocalDateTime cancelledAt) {
        if (showId <= 0) {
            throw new IllegalArgumentException("Show ID must be positive.");
        }
        Objects.requireNonNull(cancelledAt, "Cancellation date is required.");
        List<BookingRefundDetails> bookings = bookingGateway.getBookingRefundDetailsForShow(showId);
        if (bookings == null) {
            throw new IllegalStateException("Booking gateway returned no booking list.");
        }
        List<Refund> refunds = new ArrayList<>();
        for (BookingRefundDetails details : bookings) {
            if (details == null) {
                throw new IllegalStateException("Booking gateway returned an empty booking record.");
            }
            if (!details.isCancellable()) {
                continue;
            }
            validateRefund(details, details.getTotalAmount());
            bookingGateway.cancelBooking(details.getBookingId());
            refunds.add(createRefund(details, details.getTotalAmount(), SHOW_CANCELLATION_REASON, cancelledAt));
        }
        return refunds;
    }

    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    private void validateRefund(BookingRefundDetails details, BigDecimal amount) {
        if (refundRepository.findByBookingId(details.getBookingId()).isPresent()) {
            throw new IllegalStateException("A refund already exists for booking " + details.getBookingId());
        }
        Payment payment = paymentService.findPayment(details.getPaymentId())
                .orElseThrow(() -> new IllegalStateException("Payment record not found for booking "
                        + details.getBookingId()));
        if (payment.getBookingId() != details.getBookingId()
                || payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Booking does not have a successful payment to refund.");
        }
        if (payment.getAmount().compareTo(details.getTotalAmount()) != 0
                || amount.signum() <= 0 || amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalStateException("Refund details do not match the successful payment.");
        }
    }

    private Refund createRefund(BookingRefundDetails details, BigDecimal amount,
            String reason, LocalDateTime refundDate) {
        paymentService.markRefunded(details.getPaymentId(), amount);
        return refundRepository.save(new Refund(details.getBookingId(), details.getPaymentId(),
                amount, refundDate, reason));
    }
}
