package com.movie.repository;

import com.movie.models.offer.Offer;
import com.movie.models.offer.OfferRedemption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryOfferRepository implements OfferRepository {
    private final Map<String, Offer> offers = new HashMap<>();
    private final List<OfferRedemption> redemptions = new ArrayList<>();
    private final Map<Integer, AppliedOffer> applicationsByBooking = new HashMap<>();
    private final AtomicInteger nextRedemptionId = new AtomicInteger(1);

    @Override
    public synchronized Offer save(Offer offer) {
        if (offer == null) {
            throw new IllegalArgumentException("Offer is required.");
        }
        if (offers.containsKey(offer.getCode())) {
            throw new IllegalArgumentException("Offer code already exists: " + offer.getCode());
        }
        offers.put(offer.getCode(), offer);
        return offer;
    }

    @Override
    public synchronized Optional<Offer> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(offers.get(code.trim().toUpperCase(Locale.ROOT)));
    }

    @Override
    public synchronized List<Offer> findAll() {
        return new ArrayList<>(offers.values());
    }

    @Override
    public synchronized boolean hasUserRedeemed(String offerCode, int userId) {
        String normalizedCode = normalize(offerCode);
        return redemptions.stream().anyMatch(redemption ->
                redemption.getOfferCode().equals(normalizedCode) && redemption.getUserId() == userId);
    }

    @Override
    public synchronized void reserveOfferForBooking(int bookingId, int userId, String offerCode) {
        if (bookingId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Booking ID and user ID must be positive.");
        }
        String normalizedCode = normalize(offerCode);
        if (!offers.containsKey(normalizedCode)) {
            throw new IllegalArgumentException("Offer does not exist: " + normalizedCode);
        }
        if (applicationsByBooking.containsKey(bookingId)) {
            throw new IllegalStateException("Only one offer can be applied to a booking.");
        }
        if (hasUserRedeemed(normalizedCode, userId) || applicationsByBooking.values().stream()
                .anyMatch(application -> application.reserved
                        && application.offerCode.equals(normalizedCode)
                        && application.userId == userId)) {
            throw new IllegalStateException("The user has already redeemed or reserved this offer.");
        }
        applicationsByBooking.put(bookingId, new AppliedOffer(normalizedCode, userId));
    }

    @Override
    public synchronized boolean recordRedemption(int bookingId, int userId, LocalDateTime redeemedAt) {
        AppliedOffer application = applicationsByBooking.get(bookingId);
        if (application == null || !application.reserved) {
            return false;
        }
        if (application.userId != userId) {
            throw new IllegalStateException("Offer application belongs to a different user.");
        }
        if (hasUserRedeemed(application.offerCode, userId)) {
            throw new IllegalStateException("The user has already redeemed this offer.");
        }
        redemptions.add(new OfferRedemption(nextRedemptionId.getAndIncrement(),
                application.offerCode, userId, bookingId, redeemedAt));
        application.reserved = false;
        return true;
    }

    @Override
    public synchronized void releaseOfferReservation(int bookingId) {
        AppliedOffer application = applicationsByBooking.get(bookingId);
        if (application != null) {
            application.reserved = false;
        }
    }

    @Override
    public synchronized List<OfferRedemption> findRedemptionsByUserId(int userId) {
        List<OfferRedemption> userRedemptions = new ArrayList<>();
        for (OfferRedemption redemption : redemptions) {
            if (redemption.getUserId() == userId) {
                userRedemptions.add(redemption);
            }
        }
        return userRedemptions;
    }

    private static String normalize(String offerCode) {
        if (offerCode == null || offerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Offer code is required.");
        }
        return offerCode.trim().toUpperCase(Locale.ROOT);
    }

    private static final class AppliedOffer {
        private final String offerCode;
        private final int userId;
        private boolean reserved = true;

        private AppliedOffer(String offerCode, int userId) {
            this.offerCode = offerCode;
            this.userId = userId;
        }
    }
}
