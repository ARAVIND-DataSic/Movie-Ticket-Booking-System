package com.movie.models.booking;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Unified Booking model.
 *
 * Supports two usage patterns:
 *
 * 1. Member 1/4 pattern – seat-list based booking with UUID string IDs,
 *    BookingStatus enum, and Instant timestamps. Used by the original
 *    BookingRepository / SeatLockService flow.
 *
 * 2. Member 3 (Show Scheduling) pattern – single-seat booking with integer IDs,
 *    String status, convenience fee, and LocalDateTime timestamps. Used by
 *    BookingService.createBooking() and RefundService.
 */
public class Booking {

    // ── Member 1/4 fields ─────────────────────────────────────────────────────
    private final String id;
    private final String stringUserId;
    private final String stringShowId;
    private final List<String> seatIds;
    private final Instant createdAt;
    private BookingStatus bookingStatus;

    // ── Member 3 fields ───────────────────────────────────────────────────────
    private int bookingId;
    private int intShowId;
    private String userId;
    private String seatNumber;
    private String seatType;
    private double ticketPrice;
    private double convenienceFee;
    private double totalAmount;
    private String status; // "CONFIRMED" | "CANCELLED"
    private LocalDateTime bookingTime;

    // ── Discriminator ─────────────────────────────────────────────────────────
    private final boolean legacy; // true = Member 1/4 constructor was used

    // =========================================================================
    // Member 1/4 constructor  (UUID-based, seat-list)
    // =========================================================================
    public Booking(String userId, String showId, List<String> seatIds) {
        this.legacy        = true;
        this.id            = UUID.randomUUID().toString();
        this.stringUserId  = userId;
        this.stringShowId  = showId;
        this.seatIds       = List.copyOf(seatIds);
        this.createdAt     = Instant.now();
        this.bookingStatus = BookingStatus.CONFIRMED;

        // keep String-status field in sync
        this.status = BookingStatus.CONFIRMED.name();

        // unused Member 3 finals need non-null defaults
        this.bookingTime   = null;
    }

    // =========================================================================
    // Member 3 constructors  (int-based, single seat)
    // =========================================================================
    public Booking() {
        this.legacy       = false;
        this.id           = UUID.randomUUID().toString();
        this.stringUserId = null;
        this.stringShowId = null;
        this.seatIds      = Collections.emptyList();
        this.createdAt    = null;
        this.status       = "CONFIRMED";
        this.bookingTime  = LocalDateTime.now();
    }

    public Booking(int bookingId, int showId, String userId,
                   String seatNumber, String seatType,
                   double ticketPrice, double convenienceFee) {
        this.legacy          = false;
        this.id              = UUID.randomUUID().toString();
        this.stringUserId    = userId;
        this.stringShowId    = String.valueOf(showId);
        this.seatIds         = Collections.emptyList();
        this.createdAt       = null;
        this.bookingId       = bookingId;
        this.intShowId       = showId;
        this.userId          = userId;
        this.seatNumber      = seatNumber;
        this.seatType        = seatType;
        this.ticketPrice     = ticketPrice;
        this.convenienceFee  = convenienceFee;
        this.totalAmount     = ticketPrice + convenienceFee;
        this.status          = "CONFIRMED";
        this.bookingStatus   = BookingStatus.CONFIRMED;
        this.bookingTime     = LocalDateTime.now();
    }

    // =========================================================================
    // Member 1/4 accessors
    // =========================================================================
    public String getId()                        { return id; }
    public String getStringUserId()              { return stringUserId != null ? stringUserId : userId; }
    public String getStringShowId()              { return stringShowId != null ? stringShowId : String.valueOf(intShowId); }
    public List<String> getSeatIds()             { return seatIds; }
    public Instant getCreatedAt()                { return createdAt; }
    public BookingStatus getBookingStatus()      { return bookingStatus != null ? bookingStatus : BookingStatus.valueOf(status); }

    /** Cancel booking (Member 1/4 style). */
    public void cancel() {
        this.bookingStatus = BookingStatus.CANCELLED;
        this.status        = "CANCELLED";
    }

    // =========================================================================
    // Member 3 accessors
    // =========================================================================
    public int getBookingId()                    { return bookingId; }
    public void setBookingId(int bookingId)      { this.bookingId = bookingId; }

    /** Returns the numeric show ID (Member 3). */
    public int getShowId()                       { return intShowId; }
    public void setShowId(int showId)            { this.intShowId = showId; }

    public String getUserId()                    { return userId != null ? userId : stringUserId; }
    public void setUserId(String userId)         { this.userId = userId; }

    public String getSeatNumber()                { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType()                  { return seatType; }
    public void setSeatType(String seatType)     { this.seatType = seatType; }

    public double getTicketPrice()               { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice  = ticketPrice;
        this.totalAmount  = this.ticketPrice + this.convenienceFee;
    }

    public double getConvenienceFee()            { return convenienceFee; }
    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
        this.totalAmount    = this.ticketPrice + this.convenienceFee;
    }

    public double getTotalAmount()               { return totalAmount; }

    /** String status used by Member 3 ("CONFIRMED" / "CANCELLED"). */
    public String getStatus()                    { return status; }
    public void setStatus(String status) {
        this.status        = status;
        try { this.bookingStatus = BookingStatus.valueOf(status); } catch (IllegalArgumentException ignored) {}
    }

    public LocalDateTime getBookingTime()        { return bookingTime; }
    public void setBookingTime(LocalDateTime t)  { this.bookingTime = t; }
}
