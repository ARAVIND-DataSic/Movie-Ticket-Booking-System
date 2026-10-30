package com.movie.models.offer;

import java.time.LocalDateTime;
import java.util.Objects;

public class OfferRedemption {
    private final int id;
    private final String offerCode;
    private final int userId;
    private final int bookingId;
    private final LocalDateTime redeemedAt;

    public OfferRedemption(int id, String offerCode, int userId, int bookingId, LocalDateTime redeemedAt) {
        if (id <= 0 || userId <= 0 || bookingId <= 0) {
            throw new IllegalArgumentException("Redemption, user and booking IDs must be positive.");
        }
        this.id = id;
        this.offerCode = Objects.requireNonNull(offerCode, "Offer code is required.");
        this.userId = userId;
        this.bookingId = bookingId;
        this.redeemedAt = Objects.requireNonNull(redeemedAt, "Redemption date is required.");
    }

    public int getId() {
        return id;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }
}
