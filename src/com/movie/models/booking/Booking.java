package com.movie.models.booking;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private int showId;
    private String userId;
    private String seatNumber;
    private String seatType;
    private double ticketPrice;
    private double convenienceFee;
    private double totalAmount;
    private String status; // CONFIRMED, CANCELLED
    private LocalDateTime bookingTime;

    public Booking() {
        this.status = "CONFIRMED";
        this.bookingTime = LocalDateTime.now();
    }

    public Booking(int bookingId, int showId, String userId,
                   String seatNumber, String seatType,
                   double ticketPrice, double convenienceFee) {
        this.bookingId = bookingId;
        this.showId = showId;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.ticketPrice = ticketPrice;
        this.convenienceFee = convenienceFee;
        this.totalAmount = ticketPrice + convenienceFee;
        this.status = "CONFIRMED";
        this.bookingTime = LocalDateTime.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
        this.totalAmount = this.ticketPrice + this.convenienceFee;
    }

    public double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
        this.totalAmount = this.ticketPrice + this.convenienceFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}
