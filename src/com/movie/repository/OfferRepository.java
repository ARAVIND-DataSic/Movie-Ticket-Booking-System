package com.movie.repository;

import com.movie.models.offer.Offer;
import com.movie.models.offer.OfferRedemption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findByCode(String code);

    List<Offer> findAll();

    boolean hasUserRedeemed(String offerCode, int userId);

    void reserveOfferForBooking(int bookingId, int userId, String offerCode);

    boolean recordRedemption(int bookingId, int userId, LocalDateTime redeemedAt);

    void releaseOfferReservation(int bookingId);

    List<OfferRedemption> findRedemptionsByUserId(int userId);
}
