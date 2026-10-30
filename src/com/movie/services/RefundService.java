
package com.movie.services;

import com.movie.models.booking.Booking;
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

    // Existing Booking module refund record
    public static class RefundRecord {
        private int bookingId;
        private int showId;
        private String userId;
        private double refundAmount;
        private double convenienceFeeRefunded;
        private String reason;
        private String status;
        private LocalDateTime processedAt;

        public RefundRecord(int bookingId, int showId, String userId,
                double refundAmount, double convenienceFeeRefunded,
                String reason) {
            this.bookingId = bookingId;
            this.showId = showId;
            this.userId = userId;
            this.refundAmount = refundAmount;
            this.convenienceFeeRefunded = convenienceFeeRefunded;
            this.reason = reason;
            this.status = "SUCCESS";
            this.processedAt = LocalDateTime.now();
        }

        public int getBookingId() { return bookingId; }
        public int getShowId() { return showId; }
        public String getUserId() { return userId; }
        public double getRefundAmount() { return refundAmount; }
        public double getConvenienceFeeRefunded() { return convenienceFeeRefunded; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
        public LocalDateTime getProcessedAt() { return processedAt; }
    }

    private final List<RefundRecord> refunds = new ArrayList<>();

    // Dependencies for repository-based refund processing
    private final PaymentService paymentService;
    private final BookingRefundGateway bookingGateway;
    private final RefundRepository refundRepository;

    // Existing no-argument constructor
    public RefundService() {
        this.paymentService = null;
        this.bookingGateway = null;
        this.refundRepository = null;
    }

    // Constructor for repository-based refund processing
    public RefundService(PaymentService paymentService,
            BookingRefundGateway bookingGateway,
            RefundRepository refundRepository) {
        this.paymentService = Objects.requireNonNull(paymentService,
                "Payment service is required.");
        this.bookingGateway = Objects.requireNonNull(bookingGateway,
                "Booking gateway is required.");
        this.refundRepository = Objects.requireNonNull(refundRepository,
                "Refund repository is required.");
    }

    // Existing Booking module refund processing
    public RefundRecord processRefund(Booking booking, String reason) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        double fullAmount = booking.getTotalAmount();

        RefundRecord record = new RefundRecord(
                booking.getBookingId(),
                booking.getShowId(),
                booking.getUserId(),
                fullAmount,
                booking.getConvenienceFee(),
                reason
        );

        refunds.add(record);
        return record;
    }

    public List<RefundRecord> getAllRefunds() {
        return new ArrayList<>(refunds);
    }

    public RefundRecord getRefundForBooking(int bookingId) {
        for (RefundRecord refund : refunds) {
            if (refund.getBookingId() == bookingId) {
                return refund;
            }
        }
        return null;
    }

    public List<RefundRecord> getRefundsForShow(int showId) {
        List<RefundRecord> result = new ArrayList<>();

        for (RefundRecord refund : refunds) {
            if (refund.getShowId() == showId) {
                result.add(refund);
            }
        }

        return result;
    }

    // Customer cancellation with repository-based refund
    public Refund cancelBookingByCustomer(int bookingId,
            LocalDateTime cancelledAt) {

        requireRepositoryDependencies();
        Objects.requireNonNull(cancelledAt,
                "Cancellation date is required.");

        BookingRefundDetails details =
                bookingGateway.getBookingRefundDetails(bookingId);

        if (details == null || details.getBookingId() != bookingId) {
            throw new IllegalArgumentException(
                    "Booking details not found: " + bookingId);
        }

        if (!details.isCancellable()) {
            throw new IllegalStateException(
                    "Booking is not active and cannot be cancelled.");
        }

        LocalDateTime deadline = details.getShowtime().minusHours(4);

        if (cancelledAt.isAfter(deadline)) {
            throw new IllegalStateException(
                    "Booking cancellation is not allowed within four hours of showtime.");
        }

        validateRefund(details, details.getTicketAmount());

        bookingGateway.cancelBooking(bookingId);

        return createRefund(
                details,
                details.getTicketAmount(),
                "Customer cancellation",
                cancelledAt
        );
    }

    // Admin show cancellation with full refunds
    public List<Refund> cancelShowByAdmin(int showId,
            LocalDateTime cancelledAt) {

        requireRepositoryDependencies();

        if (showId <= 0) {
            throw new IllegalArgumentException("Show ID must be positive.");
        }

        Objects.requireNonNull(cancelledAt,
                "Cancellation date is required.");

        List<BookingRefundDetails> bookings =
                bookingGateway.getBookingRefundDetailsForShow(showId);

        if (bookings == null) {
            throw new IllegalStateException(
                    "Booking gateway returned no booking list.");
        }

        List<Refund> showRefunds = new ArrayList<>();

        for (BookingRefundDetails details : bookings) {

            if (details == null) {
                throw new IllegalStateException(
                        "Booking gateway returned an empty booking record.");
            }

            if (!details.isCancellable()) {
                continue;
            }

            validateRefund(details, details.getTotalAmount());

            bookingGateway.cancelBooking(details.getBookingId());

            showRefunds.add(
                    createRefund(
                            details,
                            details.getTotalAmount(),
                            "Show cancelled by administrator",
                            cancelledAt
                    )
            );
        }

        return showRefunds;
    }

    // Repository-based refund history
    public List<Refund> getRepositoryRefunds() {
        requireRepositoryDependencies();
        return refundRepository.findAll();
    }

    private void validateRefund(BookingRefundDetails details,
            BigDecimal amount) {

        if (refundRepository.findByBookingId(details.getBookingId())
                .isPresent()) {
            throw new IllegalStateException(
                    "A refund already exists for booking "
                            + details.getBookingId());
        }

        Payment payment = paymentService.findPayment(details.getPaymentId())
                .orElseThrow(() -> new IllegalStateException(
                        "Payment record not found for booking "
                                + details.getBookingId()));

        if (payment.getBookingId() != details.getBookingId()
                || payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Booking does not have a successful payment to refund.");
        }

        if (payment.getAmount().compareTo(details.getTotalAmount()) != 0
                || amount.signum() <= 0
                || amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalStateException(
                    "Refund details do not match the successful payment.");
        }
    }

    private Refund createRefund(BookingRefundDetails details,
            BigDecimal amount, String reason,
            LocalDateTime refundDate) {

        paymentService.markRefunded(details.getPaymentId(), amount);

        return refundRepository.save(
                new Refund(
                        details.getBookingId(),
                        details.getPaymentId(),
                        amount,
                        refundDate,
                        reason
                )
        );
    }

    private void requireRepositoryDependencies() {
        if (paymentService == null
                || bookingGateway == null
                || refundRepository == null) {
            throw new IllegalStateException(
                    "Repository-based refund dependencies are not configured.");
        }
    }
}
