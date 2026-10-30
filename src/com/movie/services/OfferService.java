package com.movie.services;

import com.movie.models.offer.Offer;
import com.movie.models.offer.OfferRedemption;
import com.movie.repository.OfferRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OfferService {
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = Objects.requireNonNull(offerRepository, "Offer repository is required.");
    }

    public Offer createOffer(Offer offer) {
        return offerRepository.save(Objects.requireNonNull(offer, "Offer is required."));
    }

    public Optional<Offer> findOffer(String offerCode) {
        return offerRepository.findByCode(offerCode);
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public BigDecimal applyOffer(String offerCode, int bookingId, int userId,
            BigDecimal bookingAmount, LocalDateTime appliedAt) {
        if (bookingAmount == null || bookingAmount.signum() < 0) {
            throw new IllegalArgumentException("Booking amount cannot be negative or null.");
        }
        Objects.requireNonNull(appliedAt, "Offer application date is required.");
        Offer offer = offerRepository.findByCode(offerCode)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found: " + offerCode));
        if (appliedAt.isBefore(offer.getValidFrom()) || appliedAt.isAfter(offer.getValidUntil())) {
            throw new IllegalArgumentException("Offer is outside its validity period.");
        }
        if (bookingAmount.compareTo(offer.getMinimumBookingAmount()) < 0) {
            throw new IllegalArgumentException("Booking amount does not meet the offer minimum.");
        }
        if (offerRepository.hasUserRedeemed(offer.getCode(), userId)) {
            throw new IllegalStateException("User has already redeemed this offer.");
        }

        BigDecimal discount = bookingAmount.multiply(offer.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .min(offer.getMaximumDiscount())
                .min(bookingAmount)
                .setScale(2, RoundingMode.HALF_UP);
        offerRepository.reserveOfferForBooking(bookingId, userId, offer.getCode());
        return discount;
    }

    public boolean completeBookingOffer(int bookingId, int userId, LocalDateTime redeemedAt) {
        return offerRepository.recordRedemption(bookingId, userId,
                Objects.requireNonNull(redeemedAt, "Redemption date is required."));
    }

    public void releaseBookingOffer(int bookingId) {
        offerRepository.releaseOfferReservation(bookingId);
    }

    public List<OfferRedemption> getRedemptionsForUser(int userId) {
        return offerRepository.findRedemptionsByUserId(userId);
    }
}
