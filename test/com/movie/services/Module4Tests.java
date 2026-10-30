package com.movie.services;

import com.movie.models.offer.Offer;
import com.movie.models.payment.Payment;
import com.movie.models.payment.PaymentMethod;
import com.movie.models.payment.PaymentStatus;
import com.movie.models.refund.Refund;
import com.movie.payment.CardPayment;
import com.movie.payment.NetBankingPayment;
import com.movie.payment.UpiPayment;
import com.movie.repository.InMemoryOfferRepository;
import com.movie.repository.InMemoryPaymentRepository;
import com.movie.repository.InMemoryRefundRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Module4Tests {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 30, 12, 0);
    private static int passed;

    private Module4Tests() {
    }

    public static void main(String[] args) {
        testPaymentMethods();
        testFailedPaymentReleasesSeats();
        testPaymentCalculation();
        testOfferApplicationRules();
        testOfferDiscountCap();
        testOfferReuseAndOnePerBooking();
        testCustomerCancellationBeforeDeadline();
        testCustomerCancellationAfterDeadline();
        testAdminShowCancellationRefundsAll();
        testPaymentHistory();
        System.out.println("All " + passed + " Module 4 tests passed.");
    }

    private static void testPaymentMethods() {
        Fixture fixture = new Fixture();
        check(fixture.pay(1, PaymentMethod.UPI, new UpiPayment("member@bank")).getStatus()
                == PaymentStatus.SUCCESS, "UPI payment succeeds");
        check(fixture.pay(2, PaymentMethod.CARD, new CardPayment("SIMULATED-CARD")).getStatus()
                == PaymentStatus.SUCCESS, "Card payment succeeds");
        check(fixture.pay(3, PaymentMethod.NET_BANKING, new NetBankingPayment("Example Bank")).getStatus()
                == PaymentStatus.SUCCESS, "Net Banking payment succeeds");
    }

    private static void testFailedPaymentReleasesSeats() {
        Fixture fixture = new Fixture();
        Payment payment = fixture.pay(4, PaymentMethod.UPI, new UpiPayment("invalid"));
        check(payment.getStatus() == PaymentStatus.FAILED, "invalid simulated UPI fails");
        check(fixture.bookingGateway.failedBookingIds.contains(4), "failed payment marks booking failed");
        check(fixture.bookingGateway.releasedSeatBookingIds.contains(4), "failed payment releases locked seats");
    }

    private static void testPaymentCalculation() {
        Fixture fixture = new Fixture();
        BigDecimal total = fixture.paymentService.calculateTotalAmount(
                Arrays.asList(new BigDecimal("150.00"), new BigDecimal("150.00")),
                new BigDecimal("50.00"));
        check(total.compareTo(new BigDecimal("297.20")) == 0, "total includes convenience fees, GST and discount");
    }

    private static void testOfferApplicationRules() {
        Fixture fixture = new Fixture();
        fixture.addOffer("VALID", "20", "100", "100");
        BigDecimal discount = fixture.offerService.applyOffer("valid", 10, 1,
                new BigDecimal("200.00"), NOW);
        check(discount.compareTo(new BigDecimal("40.00")) == 0, "valid offer applies");
        fixture.offerService.releaseBookingOffer(10);

        fixture.addOffer("EXPIRED", "20", "100", "0", NOW.minusDays(3), NOW.minusDays(1));
        expectThrows(IllegalArgumentException.class, () -> fixture.offerService.applyOffer(
                "EXPIRED", 11, 1, new BigDecimal("200.00"), NOW), "expired offer is rejected");
        fixture.addOffer("MINIMUM", "20", "100", "250");
        expectThrows(IllegalArgumentException.class, () -> fixture.offerService.applyOffer(
                "MINIMUM", 12, 1, new BigDecimal("200.00"), NOW), "minimum amount is enforced");
    }

    private static void testOfferDiscountCap() {
        Fixture fixture = new Fixture();
        fixture.addOffer("CAPPED", "50", "30", "0");
        BigDecimal discount = fixture.offerService.applyOffer("CAPPED", 20, 1,
                new BigDecimal("100.00"), NOW);
        check(discount.compareTo(new BigDecimal("30.00")) == 0, "discount is capped at maximum amount");
    }

    private static void testOfferReuseAndOnePerBooking() {
        Fixture fixture = new Fixture();
        fixture.addOffer("ONCE", "10", "50", "0");
        fixture.addOffer("SECOND", "10", "50", "0");
        fixture.addOffer("FAILED", "10", "50", "0");
        fixture.offerService.applyOffer("ONCE", 30, 2, new BigDecimal("100.00"), NOW);
        expectThrows(IllegalStateException.class, () -> fixture.offerService.applyOffer(
                "SECOND", 30, 2, new BigDecimal("100.00"), NOW),
                "only one offer applies to a booking");
        check(fixture.offerService.completeBookingOffer(30, 2, NOW), "successful booking records redemption");
        expectThrows(IllegalStateException.class, () -> fixture.offerService.applyOffer(
                "ONCE", 31, 2, new BigDecimal("100.00"), NOW),
                "same user cannot redeem offer twice");
        check(fixture.offerService.getRedemptionsForUser(2).size() == 1, "offer redemption history is stored");

        fixture.offerService.applyOffer("FAILED", 32, 3, new BigDecimal("100.00"), NOW);
        Payment failedPayment = fixture.paymentService.createPayment(32, 3, new BigDecimal("100.00"),
                PaymentMethod.UPI, NOW);
        fixture.paymentService.processPayment(failedPayment.getId(), new UpiPayment("invalid"), NOW);
        expectThrows(IllegalStateException.class, () -> fixture.offerService.applyOffer(
                "SECOND", 32, 3, new BigDecimal("100.00"), NOW),
                "failed payment does not allow a second offer on the same booking");
        check(fixture.offerService.applyOffer("FAILED", 33, 3, new BigDecimal("100.00"), NOW)
                .compareTo(new BigDecimal("10.00")) == 0,
                "failed payment releases user offer reservation");
    }

    private static void testCustomerCancellationBeforeDeadline() {
        Fixture fixture = new Fixture();
        BookingRefundDetails details = fixture.addRefundableBooking(
                40, 4, 400, new BigDecimal("300.00"), new BigDecimal("347.20"),
                NOW.plusHours(4), true);
        Refund refund = fixture.refundService.cancelBookingByCustomer(40, NOW);
        check(refund.getAmount().compareTo(new BigDecimal("300.00")) == 0,
                "customer may cancel exactly four hours before showtime");
        check(refund.getPaymentId() == details.getPaymentId(), "refund references original payment");
        check(fixture.paymentService.findPayment(details.getPaymentId()).get().getStatus()
                == PaymentStatus.PARTIALLY_REFUNDED, "ticket-only refund marks payment partially refunded");
        check(fixture.bookingGateway.cancelledBookingIds.contains(40), "customer cancellation updates booking");
    }

    private static void testCustomerCancellationAfterDeadline() {
        Fixture fixture = new Fixture();
        fixture.addRefundableBooking(41, 5, 401, new BigDecimal("100.00"),
                new BigDecimal("147.20"), NOW.plusHours(4), true);
        expectThrows(IllegalStateException.class, () -> fixture.refundService
                .cancelBookingByCustomer(41, NOW.plusMinutes(1)),
                "customer cancellation inside four-hour deadline is rejected");
        check(fixture.bookingGateway.cancelledBookingIds.isEmpty(),
                "rejected cancellation leaves booking unchanged");
    }

    private static void testAdminShowCancellationRefundsAll() {
        Fixture fixture = new Fixture();
        BookingRefundDetails first = fixture.addRefundableBooking(50, 6, 500,
                new BigDecimal("300.00"), new BigDecimal("347.20"), NOW.plusHours(2), true);
        BookingRefundDetails second = fixture.addRefundableBooking(51, 7, 501,
                new BigDecimal("150.00"), new BigDecimal("174.80"), NOW.plusHours(2), true);
        fixture.bookingGateway.showBookings.put(99, Arrays.asList(first, second));
        List<Refund> refunds = fixture.refundService.cancelShowByAdmin(99, NOW);
        check(refunds.size() == 2, "admin cancellation refunds every active booking");
        check(refunds.get(0).getAmount().compareTo(new BigDecimal("347.20")) == 0
                        && refunds.get(1).getAmount().compareTo(new BigDecimal("174.80")) == 0,
                "admin receives full booking amounts including fees");
        check(fixture.bookingGateway.cancelledBookingIds.containsAll(Arrays.asList(50, 51)),
                "admin cancellation cancels every associated booking");
    }

    private static void testPaymentHistory() {
        Fixture fixture = new Fixture();
        fixture.pay(60, PaymentMethod.UPI, new UpiPayment("history@bank"));
        fixture.pay(60, PaymentMethod.CARD, new CardPayment("SIMULATED-CARD"));
        check(fixture.paymentService.getPaymentHistory(60).size() == 2,
                "payment history returns all attempts for a booking");
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError("FAIL: " + name);
        }
        passed++;
        System.out.println("PASS: " + name);
    }

    private static void expectThrows(Class<? extends Throwable> exceptionType, Runnable operation, String name) {
        try {
            operation.run();
        } catch (Throwable actual) {
            if (exceptionType.isInstance(actual)) {
                passed++;
                System.out.println("PASS: " + name);
                return;
            }
            throw new AssertionError("Expected " + exceptionType.getSimpleName() + " for " + name, actual);
        }
        throw new AssertionError("Expected " + exceptionType.getSimpleName() + " for " + name);
    }

    private static final class Fixture {
        private final FakeBookingGateway bookingGateway = new FakeBookingGateway();
        private final InMemoryOfferRepository offerRepository = new InMemoryOfferRepository();
        private final OfferService offerService = new OfferService(offerRepository);
        private final InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
        private final PaymentService paymentService = new PaymentService(
                paymentRepository, bookingGateway, offerService);
        private final RefundService refundService = new RefundService(
                paymentService, bookingGateway, new InMemoryRefundRepository());

        private Payment pay(int bookingId, PaymentMethod method, com.movie.payment.PaymentMethod processor) {
            Payment payment = paymentService.createPayment(bookingId, 1, new BigDecimal("100.00"),
                    method, NOW);
            return paymentService.processPayment(payment.getId(), processor, NOW);
        }

        private void addOffer(String code, String percentage, String maxDiscount, String minimum) {
            addOffer(code, percentage, maxDiscount, minimum, NOW.minusDays(1), NOW.plusDays(1));
        }

        private void addOffer(String code, String percentage, String maxDiscount, String minimum,
                LocalDateTime start, LocalDateTime end) {
            offerService.createOffer(new Offer(code, new BigDecimal(percentage), new BigDecimal(maxDiscount),
                    start, end, new BigDecimal(minimum)));
        }

        private BookingRefundDetails addRefundableBooking(int bookingId, int userId, int showId,
                BigDecimal ticketAmount, BigDecimal totalAmount, LocalDateTime showtime, boolean cancellable) {
            Payment payment = paymentService.createPayment(bookingId, userId, totalAmount,
                    PaymentMethod.CARD, NOW);
            paymentService.processPayment(payment.getId(), new CardPayment("SIMULATED-CARD"), NOW);
            BookingRefundDetails details = new BookingRefundDetails(bookingId, payment.getId(),
                    showtime, ticketAmount, totalAmount, cancellable);
            bookingGateway.bookings.put(bookingId, details);
            bookingGateway.showBookings.computeIfAbsent(showId, unused -> new ArrayList<>()).add(details);
            return details;
        }
    }

    private static final class FakeBookingGateway
            implements BookingPaymentGateway, BookingRefundGateway {
        private final List<Integer> confirmedBookingIds = new ArrayList<>();
        private final List<Integer> failedBookingIds = new ArrayList<>();
        private final List<Integer> releasedSeatBookingIds = new ArrayList<>();
        private final List<Integer> cancelledBookingIds = new ArrayList<>();
        private final Map<Integer, BookingRefundDetails> bookings = new HashMap<>();
        private final Map<Integer, List<BookingRefundDetails>> showBookings = new HashMap<>();

        @Override
        public void confirmBooking(int bookingId) {
            confirmedBookingIds.add(bookingId);
        }

        @Override
        public void markBookingFailed(int bookingId) {
            failedBookingIds.add(bookingId);
        }

        @Override
        public void releaseLockedSeats(int bookingId) {
            releasedSeatBookingIds.add(bookingId);
        }

        @Override
        public BookingRefundDetails getBookingRefundDetails(int bookingId) {
            return bookings.get(bookingId);
        }

        @Override
        public List<BookingRefundDetails> getBookingRefundDetailsForShow(int showId) {
            return showBookings.getOrDefault(showId, new ArrayList<>());
        }

        @Override
        public void cancelBooking(int bookingId) {
            cancelledBookingIds.add(bookingId);
        }
    }
}
