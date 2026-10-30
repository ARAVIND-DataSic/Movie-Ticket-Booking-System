package com.movie.models.show;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Show {

    private int showId;
    private String movieId;
    private String screenId;
    private LocalDate showDate;
    private LocalTime startTime;
    private int movieDuration;

    private double regularPrice;
    private double premiumPrice;
    private double reclinerPrice;

    private String status;
    private int bookingCount;

    public Show(int showId, String movieId, String screenId,
                LocalDate showDate, LocalTime startTime,
                int movieDuration, double regularPrice,
                double premiumPrice, double reclinerPrice) {

        this.showId = showId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.showDate = showDate;
        this.startTime = startTime;
        this.movieDuration = movieDuration;
        this.regularPrice = regularPrice;
        this.premiumPrice = premiumPrice;
        this.reclinerPrice = reclinerPrice;
        this.status = "SCHEDULED";
        this.bookingCount = 0;
    }

    public int getShowId() {
        return showId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        if (hasBookings()) {
            throw new IllegalStateException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }
        this.movieId = movieId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        if (hasBookings()) {
            throw new IllegalStateException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }
        this.screenId = screenId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        if (hasBookings()) {
            throw new IllegalStateException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }
        this.showDate = showDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if (hasBookings()) {
            throw new IllegalStateException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }
        this.startTime = startTime;
    }

    public int getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(int movieDuration) {
        if (hasBookings()) {
            throw new IllegalStateException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }
        this.movieDuration = movieDuration;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime start =
                LocalDateTime.of(showDate, startTime);

        return start.plusMinutes(movieDuration);
    }

    public double getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(double regularPrice) {
        this.regularPrice = regularPrice;
    }

    public double getPremiumPrice() {
        return premiumPrice;
    }

    public void setPremiumPrice(double premiumPrice) {
        this.premiumPrice = premiumPrice;
    }

    public double getReclinerPrice() {
        return reclinerPrice;
    }

    public void setReclinerPrice(double reclinerPrice) {
        this.reclinerPrice = reclinerPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasBookings() {
        return bookingCount > 0;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    public void addBooking() {
        this.bookingCount++;
    }

    public void cancelAllBookings() {
        this.bookingCount = 0;
    }
}
