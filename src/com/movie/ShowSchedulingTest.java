package com.movie;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.movie.models.booking.Booking;
import com.movie.models.show.Show;
import com.movie.repository.ShowRepository;
import com.movie.services.BookingService;
import com.movie.services.RefundService;
import com.movie.services.ShowService;

public class ShowSchedulingTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("   SHOW SCHEDULING TEST REPORT");
        System.out.println("==================================");

        // Original 9 tests (Preserved)
        testValidShow();
        testPastDate();
        testReleaseDate();
        testOverlappingShows();
        testCleaningGap();
        testExactCleaningGap();
        testDifferentScreens();
        testUnauthorizedUser();
        testCancelledShow();

        // Additional Required Tests
        testDuplicateShowId();
        testInvalidTicketPrices();
        testCancelledShowUpdateRestriction();
        testUnauthorizedCancellation();
        testBookedShowUpdateRestriction();
        testFullRefundCoordinationWhenIntegrated();
        testDifferentDateScheduling();
        testCleaningGapBoundaryCases();
        testRoleAuthorizations();

        System.out.println("\n==================================");
        System.out.println("PASSED: " + passed);
        System.out.println("FAILED: " + failed);
        System.out.println("==================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    static ShowService createService() {
        ShowRepository repository = new ShowRepository();
        return new ShowService(repository);
    }

    static void addShow(
            ShowService service,
            int id,
            String screen,
            LocalDate date,
            LocalTime time,
            int duration) {

        service.createShow(
                "ADMIN",
                id,
                "MOVIE-101",
                date,
                screen,
                date,
                time,
                duration,
                150,
                200,
                250
        );
    }

    static void test(String name, Runnable action) {

        try {
            action.run();
            System.out.println("[PASS] " + name);
            passed++;

        } catch (Throwable error) {
            System.out.println("[FAIL] " + name);
            System.out.println("       " + error.getMessage());
            failed++;
        }
    }

    static void expectFailure(
            String expectedMessage, Runnable action) {

        try {
            action.run();

        } catch (IllegalArgumentException | IllegalStateException error) {

            if (error.getMessage() != null && error.getMessage().toLowerCase().contains(expectedMessage.toLowerCase())) {
                return;
            }

            throw new AssertionError(
                    "Unexpected error message: " + error.getMessage() + ", expected to contain: " + expectedMessage);
        }

        throw new AssertionError("Expected validation failure containing: " + expectedMessage);
    }

    // Test 1: Valid show creation
    static void testValidShow() {

        test("Valid Show Creation", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 1, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            if (service.getAllShows().size() != 1) {
                throw new AssertionError("Show not stored");
            }
        });
    }

    // Test 2: Past date
    static void testPastDate() {

        test("Past Date Rejection", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().minusDays(1);

            expectFailure("past date or time", () ->
                addShow(service, 2, "SCREEN-1",
                        date, LocalTime.of(10, 0), 120)
            );
        });
    }

    // Test 3: Before movie release
    static void testReleaseDate() {

        test("Movie Release Date Validation", () -> {

            ShowService service = createService();

            LocalDate showDate = LocalDate.now().plusDays(10);
            LocalDate releaseDate = showDate.plusDays(2);

            expectFailure("before movie release date", () -> {

                service.createShow(
                        "ADMIN", 3, "MOVIE-101",
                        releaseDate, "SCREEN-1",
                        showDate, LocalTime.of(10, 0),
                        120, 150, 200, 250
                );
            });
        });
    }

    // Test 4: Overlapping shows
    static void testOverlappingShows() {

        test("Overlapping Show Rejection", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 4, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            expectFailure("overlaps", () ->
                addShow(service, 5, "SCREEN-1",
                        date, LocalTime.of(11, 0), 120)
            );
        });
    }

    // Test 5: Less than 30 minutes gap
    static void testCleaningGap() {

        test("Less Than 30 Minutes Gap Rejection", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 6, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            expectFailure("cleaning gap", () ->
                addShow(service, 7, "SCREEN-1",
                        date, LocalTime.of(12, 29), 120)
            );
        });
    }

    // Test 6: Exactly 30 minutes gap
    static void testExactCleaningGap() {

        test("Exactly 30 Minutes Gap Acceptance", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 8, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            addShow(service, 9, "SCREEN-1",
                    date, LocalTime.of(12, 30), 120);

            if (service.getAllShows().size() != 2) {
                throw new AssertionError("Second show not created");
            }
        });
    }

    // Test 7: Different screens
    static void testDifferentScreens() {

        test("Same Time Different Screens Acceptance", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 10, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            addShow(service, 11, "SCREEN-2",
                    date, LocalTime.of(10, 0), 120);

            if (service.getAllShows().size() != 2) {
                throw new AssertionError("Show creation failed");
            }
        });
    }

    // Test 8: Unauthorized user
    static void testUnauthorizedUser() {

        test("Unauthorized User Rejection", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            expectFailure("Only Admin or Theatre Manager", () -> {

                service.createShow(
                        "CUSTOMER", 12, "MOVIE-101",
                        date, "SCREEN-1",
                        date, LocalTime.of(10, 0),
                        120, 150, 200, 250
                );
            });
        });
    }

    // Test 9: Cancelled show scheduling
    static void testCancelledShow() {

        test("Cancelled Show Slot Reuse", () -> {

            ShowService service = createService();

            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 13, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            service.cancelShow("ADMIN", 13);

            addShow(service, 14, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120);

            if (service.getAllShows().size() != 2) {
                throw new AssertionError("Cancelled slot was not released");
            }
        });
    }

    // Test 10: Duplicate show ID prevention
    static void testDuplicateShowId() {

        test("Duplicate Show ID Prevention", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 100, "SCREEN-1", date, LocalTime.of(10, 0), 120);

            expectFailure("already exists", () ->
                addShow(service, 100, "SCREEN-2", date, LocalTime.of(14, 0), 120)
            );
        });
    }

    // Test 11: Invalid ticket prices
    static void testInvalidTicketPrices() {

        test("Invalid Ticket Prices Rejection", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            // Negative regular price
            expectFailure("greater than zero", () ->
                service.createShow("ADMIN", 101, "M1", date, "S1", date,
                        LocalTime.of(10, 0), 120, -50, 200, 250)
            );

            // Zero premium price
            expectFailure("greater than zero", () ->
                service.createShow("ADMIN", 102, "M1", date, "S1", date,
                        LocalTime.of(10, 0), 120, 150, 0, 250)
            );

            // Update with negative price
            addShow(service, 103, "SCREEN-1", date, LocalTime.of(10, 0), 120);
            expectFailure("greater than zero", () ->
                service.updateShowPrices("ADMIN", 103, 150, -20, 250)
            );
        });
    }

    // Test 12: Cancelled show update restriction
    static void testCancelledShowUpdateRestriction() {

        test("Cancelled Show Update Restriction", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 200, "SCREEN-1", date, LocalTime.of(10, 0), 120);
            service.cancelShow("ADMIN", 200);

            // Attempt price update on cancelled show
            expectFailure("cancelled show", () ->
                service.updateShowPrices("ADMIN", 200, 180, 220, 280)
            );

            // Attempt schedule update on cancelled show
            expectFailure("cancelled show", () ->
                service.updateShowSchedule("ADMIN", 200, date, "SCREEN-1", date, LocalTime.of(14, 0), 120)
            );

            // Attempt re-cancellation
            expectFailure("already cancelled", () ->
                service.cancelShow("ADMIN", 200)
            );
        });
    }

    // Test 13: Unauthorized cancellation
    static void testUnauthorizedCancellation() {

        test("Unauthorized Cancellation Rejection", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            addShow(service, 300, "SCREEN-1", date, LocalTime.of(10, 0), 120);

            // Theatre Manager cannot cancel
            expectFailure("Only Admin can cancel", () ->
                service.cancelShow("THEATRE_MANAGER", 300)
            );

            // Customer cannot cancel
            expectFailure("Only Admin can cancel", () ->
                service.cancelShow("CUSTOMER", 300)
            );

            // Null role cannot cancel
            expectFailure("Only Admin can cancel", () ->
                service.cancelShow(null, 300)
            );

            // Admin can cancel
            service.cancelShow("ADMIN", 300);
            if (!"CANCELLED".equals(service.getShowById(300).getStatus())) {
                throw new AssertionError("Admin cancellation failed");
            }
        });
    }

    // Test 14: Booked show update restriction and price update for future bookings
    static void testBookedShowUpdateRestriction() {

        test("Booked Show Update Restrictions & Future Booking Pricing", () -> {

            ShowRepository repository = new ShowRepository();
            BookingService bookingService = new BookingService();
            RefundService refundService = new RefundService();
            ShowService service = new ShowService(repository, bookingService, refundService);

            LocalDate date = LocalDate.now().plusDays(10);
            Show show = service.createShow("ADMIN", 400, "M1", date, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120, 150, 200, 250);

            // Create initial booking at current price ($150 regular + $20 fee = $170)
            Booking b1 = bookingService.createBooking(1, show, "USER-1", "A1", "REGULAR", 20.0);
            if (b1.getTicketPrice() != 150.0) {
                throw new AssertionError("Initial booking did not receive original price");
            }

            // Once show has bookings, screen / schedule cannot be changed
            expectFailure("Once a show has bookings, movie, screen and start time must not be changed", () ->
                service.updateShowSchedule("ADMIN", 400, date, "SCREEN-2", date, LocalTime.of(14, 0), 120)
            );

            // Model-level setter protection
            expectFailure("Once a show has bookings, movie, screen and start time must not be changed", () ->
                show.setScreenId("SCREEN-2")
            );

            // Ticket prices CAN be updated for booked shows
            service.updateShowPrices("ADMIN", 400, 180, 240, 300);

            // Past booking must NOT be retroactively altered
            if (b1.getTicketPrice() != 150.0 || b1.getTotalAmount() != 170.0) {
                throw new AssertionError("Past booking price was incorrectly modified");
            }

            // Future booking must receive the updated price ($180 regular + $20 fee = $200)
            Booking b2 = bookingService.createBooking(2, show, "USER-2", "A2", "REGULAR", 20.0);
            if (b2.getTicketPrice() != 180.0 || b2.getTotalAmount() != 200.0) {
                throw new AssertionError("Future booking did not receive the updated price");
            }
        });
    }

    // Test 15: Full refund coordination when show is cancelled by Admin
    static void testFullRefundCoordinationWhenIntegrated() {

        test("Full Refund Coordination on Show Cancellation", () -> {

            ShowRepository repository = new ShowRepository();
            BookingService bookingService = new BookingService();
            RefundService refundService = new RefundService();
            ShowService service = new ShowService(repository, bookingService, refundService);

            LocalDate date = LocalDate.now().plusDays(10);
            Show show = service.createShow("ADMIN", 500, "M1", date, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120, 150, 200, 250);

            // Booking 1: Regular ($150) + Fee ($25) = $175
            Booking b1 = bookingService.createBooking(101, show, "USER-1", "B1", "REGULAR", 25.0);

            // Booking 2: Premium ($200) + Fee ($30) = $230
            Booking b2 = bookingService.createBooking(102, show, "USER-2", "C1", "PREMIUM", 30.0);

            // Cancel show as ADMIN
            service.cancelShow("ADMIN", 500);

            // Verify show cancelled
            if (!"CANCELLED".equals(show.getStatus())) {
                throw new AssertionError("Show was not cancelled");
            }

            // Verify bookings cancelled
            if (!"CANCELLED".equals(b1.getStatus()) || !"CANCELLED".equals(b2.getStatus())) {
                throw new AssertionError("Bookings were not cancelled upon show cancellation");
            }

            // Verify full refunds including convenience fees
            List<RefundService.RefundRecord> refunds = refundService.getRefundsForShow(500);
            if (refunds.size() != 2) {
                throw new AssertionError("Expected 2 refunds, but found " + refunds.size());
            }

            RefundService.RefundRecord r1 = refundService.getRefundForBooking(101);
            if (r1 == null || r1.getRefundAmount() != 175.0 || r1.getConvenienceFeeRefunded() != 25.0) {
                throw new AssertionError("Full refund for booking 1 was incorrect. Expected 175.0");
            }

            RefundService.RefundRecord r2 = refundService.getRefundForBooking(102);
            if (r2 == null || r2.getRefundAmount() != 230.0 || r2.getConvenienceFeeRefunded() != 30.0) {
                throw new AssertionError("Full refund for booking 2 was incorrect. Expected 230.0");
            }
        });
    }

    // Test 16: Different-date scheduling and overnight boundary cases
    static void testDifferentDateScheduling() {

        test("Different Date Scheduling & Overnight Gap Handling", () -> {

            ShowService service = createService();
            LocalDate day1 = LocalDate.now().plusDays(10);
            LocalDate day2 = day1.plusDays(1);

            // Day 1 at 10:00
            addShow(service, 601, "SCREEN-1", day1, LocalTime.of(10, 0), 120);

            // Day 2 at 10:00 on the same screen (accepted because dates are different)
            addShow(service, 602, "SCREEN-1", day2, LocalTime.of(10, 0), 120);

            // Overnight show on Day 1 starting 22:00 for 150 mins (ends Day 2 at 00:30)
            addShow(service, 603, "SCREEN-OVERNIGHT", day1, LocalTime.of(22, 0), 150);

            // Day 2 at 00:45 on SCREEN-OVERNIGHT (gap of 15 mins < 30 mins -> rejected)
            expectFailure("cleaning gap", () ->
                addShow(service, 604, "SCREEN-OVERNIGHT", day2, LocalTime.of(0, 45), 120)
            );

            // Day 2 at 01:00 on SCREEN-OVERNIGHT (exact 30-min gap -> accepted)
            addShow(service, 605, "SCREEN-OVERNIGHT", day2, LocalTime.of(1, 0), 120);
        });
    }

    // Test 17: Cleaning gap boundary cases (before and after)
    static void testCleaningGapBoundaryCases() {

        test("Cleaning Gap Boundary Cases (Before and After)", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            // Anchor show on SCREEN-BG from 14:00 to 16:00 (120 mins)
            addShow(service, 700, "SCREEN-BG", date, LocalTime.of(14, 0), 120);

            // Gap BEFORE anchor show:
            // Ends at 13:31 (gap = 29 mins) -> rejected
            expectFailure("cleaning gap", () ->
                addShow(service, 701, "SCREEN-BG", date, LocalTime.of(11, 31), 120)
            );

            // Ends at 13:30 (gap = 30 mins) -> accepted
            addShow(service, 702, "SCREEN-BG", date, LocalTime.of(11, 30), 120);

            // Gap AFTER anchor show:
            // Starts at 16:29 (gap = 29 mins) -> rejected
            expectFailure("cleaning gap", () ->
                addShow(service, 703, "SCREEN-BG", date, LocalTime.of(16, 29), 120)
            );

            // Starts at 16:30 (gap = 30 mins) -> accepted
            addShow(service, 704, "SCREEN-BG", date, LocalTime.of(16, 30), 120);
        });
    }

    // Test 18: Role authorizations
    static void testRoleAuthorizations() {

        test("Role Authorizations (Admin and Theatre Manager)", () -> {

            ShowService service = createService();
            LocalDate date = LocalDate.now().plusDays(10);

            // Theatre Manager can create show
            service.createShow("THEATRE_MANAGER", 801, "M1", date, "SCREEN-1",
                    date, LocalTime.of(10, 0), 120, 150, 200, 250);

            // Admin can create show
            service.createShow("ADMIN", 802, "M1", date, "SCREEN-2",
                    date, LocalTime.of(10, 0), 120, 150, 200, 250);

            // Customer cannot create show
            expectFailure("Only Admin or Theatre Manager", () ->
                service.createShow("CUSTOMER", 803, "M1", date, "SCREEN-3",
                        date, LocalTime.of(10, 0), 120, 150, 200, 250)
            );

            // Theatre Manager cannot cancel show
            expectFailure("Only Admin can cancel", () ->
                service.cancelShow("THEATRE_MANAGER", 801)
            );

            // Admin can cancel show
            service.cancelShow("ADMIN", 801);
            if (!"CANCELLED".equals(service.getShowById(801).getStatus())) {
                throw new AssertionError("Admin could not cancel show");
            }
        });
    }
}
