package com.movie.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.movie.models.booking.Booking;

public class RefundService {

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

    private List<RefundRecord> refunds = new ArrayList<>();

    // Full refund coordination including convenience fees
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
        for (RefundRecord r : refunds) {
            if (r.getBookingId() == bookingId) {
                return r;
            }
        }
        return null;
    }

    public List<RefundRecord> getRefundsForShow(int showId) {
        List<RefundRecord> result = new ArrayList<>();
        for (RefundRecord r : refunds) {
            if (r.getShowId() == showId) {
                result.add(r);
            }
        }
        return result;
    }
}
