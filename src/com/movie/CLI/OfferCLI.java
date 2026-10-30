package com.movie.CLI;

import com.movie.services.OfferService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OfferCLI {
    private final OfferService offerService;

    public OfferCLI(OfferService offerService) {
        this.offerService = Objects.requireNonNull(offerService, "Offer service is required.");
    }

    public BigDecimal applyOffer(String offerCode, int bookingId, int userId, BigDecimal bookingAmount) {
        BigDecimal discount = offerService.applyOffer(offerCode, bookingId, userId,
                bookingAmount, LocalDateTime.now());
        System.out.println("Offer " + offerCode + " applied. Discount: ₹" + discount);
        return discount;
    }
}
