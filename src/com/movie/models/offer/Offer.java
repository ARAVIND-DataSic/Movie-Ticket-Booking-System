package com.movie.models.offer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

public class Offer {
    private final String code;
    private final BigDecimal discountPercentage;
    private final BigDecimal maximumDiscount;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;
    private final BigDecimal minimumBookingAmount;

    public Offer(String code, BigDecimal discountPercentage, BigDecimal maximumDiscount,
            LocalDateTime validFrom, LocalDateTime validUntil, BigDecimal minimumBookingAmount) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Offer code is required.");
        }
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.discountPercentage = requireNonNegative(discountPercentage, "Discount percentage");
        if (this.discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage cannot exceed 100.");
        }
        this.maximumDiscount = requireNonNegative(maximumDiscount, "Maximum discount")
                .setScale(2, java.math.RoundingMode.HALF_UP);
        this.validFrom = Objects.requireNonNull(validFrom, "Offer start date is required.");
        this.validUntil = Objects.requireNonNull(validUntil, "Offer end date is required.");
        if (validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("Offer end date cannot be before its start date.");
        }
        this.minimumBookingAmount = requireNonNegative(minimumBookingAmount, "Minimum booking amount")
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private static BigDecimal requireNonNegative(BigDecimal amount, String fieldName) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative or null.");
        }
        return amount;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public BigDecimal getMinimumBookingAmount() {
        return minimumBookingAmount;
    }
}
