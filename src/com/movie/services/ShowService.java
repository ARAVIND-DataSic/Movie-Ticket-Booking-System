package com.movie.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.movie.models.booking.Booking;
import com.movie.models.movie.Movie;
import com.movie.models.show.Show;
import com.movie.models.theatre.Screen;
import com.movie.repository.ShowRepository;

public class ShowService {

    private ShowRepository showRepository;
    private BookingService bookingService;
    private RefundService refundService;

    // Default constructor for standalone show scheduling
    public ShowService(ShowRepository showRepository) {
        this(showRepository, null, null);
    }

    // Integrated constructor for booking and refund coordination
    public ShowService(ShowRepository showRepository,
                       BookingService bookingService,
                       RefundService refundService) {

        if (showRepository == null) {
            throw new IllegalArgumentException("ShowRepository cannot be null");
        }
        this.showRepository = showRepository;
        this.bookingService = bookingService;
        this.refundService = refundService;
    }

    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public RefundService getRefundService() {
        return refundService;
    }

    // Validate user role: Admin and Theatre Manager can create/manage shows
    public void validateRole(String role) {

        if (role == null) {
            throw new IllegalArgumentException("Invalid user role");
        }

        String userRole = role.trim().toUpperCase()
                              .replace(" ", "_");

        if (!userRole.equals("ADMIN")
                && !userRole.equals("THEATRE_MANAGER")) {

            throw new IllegalArgumentException(
                    "Only Admin or Theatre Manager can manage shows");
        }
    }

    // Create a new show
    public Show createShow(
            String role,
            int showId,
            String movieId,
            LocalDate releaseDate,
            String screenId,
            LocalDate showDate,
            LocalTime startTime,
            int movieDuration,
            double regularPrice,
            double premiumPrice,
            double reclinerPrice) {

        validateRole(role);

        if (showId <= 0) {
            throw new IllegalArgumentException("Invalid Show ID");
        }

        // Prevent duplicate show IDs
        if (showRepository.getShowById(showId) != null) {
            throw new IllegalArgumentException("Show ID already exists");
        }

        if (movieId == null || movieId.trim().isEmpty()
                || screenId == null || screenId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Movie ID and Screen ID are required");
        }

        if (releaseDate == null || showDate == null
                || startTime == null) {

            throw new IllegalArgumentException(
                    "Date and time cannot be empty");
        }

        if (movieDuration <= 0) {
            throw new IllegalArgumentException(
                    "Movie duration must be positive");
        }

        // Validate movie release date
        if (showDate.isBefore(releaseDate)) {
            throw new IllegalArgumentException(
                    "Show cannot be scheduled before movie release date");
        }

        // Validate past date and time
        LocalDateTime showStart =
                LocalDateTime.of(showDate, startTime);

        if (showStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Cannot create show for a past date or time");
        }

        // Validate ticket prices
        validatePrices(
                regularPrice, premiumPrice, reclinerPrice);

        // Calculate show end time
        LocalDateTime showEnd =
                showStart.plusMinutes(movieDuration);

        // Check screen availability with 30-minute cleaning gap
        checkScheduleAvailability(
                screenId, showStart, showEnd, -1);

        // Create show object
        Show show = new Show(
                showId,
                movieId,
                screenId,
                showDate,
                startTime,
                movieDuration,
                regularPrice,
                premiumPrice,
                reclinerPrice
        );

        // Store show
        showRepository.addShow(show);

        return show;
    }

    // Overloaded createShow accepting Movie and Screen domain models
    public Show createShow(
            String role,
            int showId,
            Movie movie,
            Screen screen,
            LocalDate showDate,
            LocalTime startTime,
            double regularPrice,
            double premiumPrice,
            double reclinerPrice) {

        if (movie == null || screen == null) {
            throw new IllegalArgumentException("Movie and Screen cannot be null");
        }

        return createShow(
                role,
                showId,
                movie.getMovieId(),
                movie.getReleaseDate(),
                screen.getScreenId(),
                showDate,
                startTime,
                movie.getDurationMinutes(),
                regularPrice,
                premiumPrice,
                reclinerPrice
        );
    }

    // Validate ticket prices: must be positive finite values
    public void validatePrices(
            double regularPrice,
            double premiumPrice,
            double reclinerPrice) {

        if (!Double.isFinite(regularPrice)
                || !Double.isFinite(premiumPrice)
                || !Double.isFinite(reclinerPrice)
                || regularPrice <= 0
                || premiumPrice <= 0
                || reclinerPrice <= 0) {

            throw new IllegalArgumentException(
                    "All ticket prices must be greater than zero");
        }
    }

    // Prevent overlapping shows and enforce 30-minute cleaning gap on same screen
    public void checkScheduleAvailability(
            String screenId,
            LocalDateTime newStart,
            LocalDateTime newEnd,
            int excludeShowId) {

        List<Show> existingShows =
                showRepository.getAllShows();

        for (Show existingShow : existingShows) {

            if (existingShow.getShowId() == excludeShowId) {
                continue;
            }

            if (!existingShow.getScreenId().equals(screenId)) {
                continue;
            }

            if ("CANCELLED".equalsIgnoreCase(existingShow.getStatus())) {
                continue;
            }

            LocalDateTime existingStart =
                    LocalDateTime.of(
                            existingShow.getShowDate(),
                            existingShow.getStartTime());

            LocalDateTime existingEnd =
                    existingShow.getEndTime();

            boolean afterExisting =
                    !newStart.isBefore(
                            existingEnd.plusMinutes(30));

            boolean beforeExisting =
                    !existingStart.isBefore(
                            newEnd.plusMinutes(30));

            if (!afterExisting && !beforeExisting) {

                throw new IllegalArgumentException(
                        "Show overlaps or minimum 30-minute cleaning gap is not available");
            }
        }
    }

    // Check if a show has bookings
    public boolean hasBookings(int showId) {
        Show show = getShowById(showId);
        if (show.hasBookings()) {
            return true;
        }
        if (bookingService != null) {
            return bookingService.hasBookingsForShow(showId);
        }
        return false;
    }

    // View all shows
    public List<Show> getAllShows() {
        return showRepository.getAllShows();
    }

    // Find show by ID
    public Show getShowById(int showId) {

        Show show = showRepository.getShowById(showId);

        if (show == null) {
            throw new IllegalArgumentException("Show not found");
        }

        return show;
    }

    // Update ticket prices (allowed for booked shows, forbidden for cancelled shows)
    public void updateShowPrices(
            String role,
            int showId,
            double regularPrice,
            double premiumPrice,
            double reclinerPrice) {

        validateRole(role);

        Show show = getShowById(showId);

        if ("CANCELLED".equalsIgnoreCase(show.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot update a cancelled show");
        }

        validatePrices(
                regularPrice, premiumPrice, reclinerPrice);

        show.setRegularPrice(regularPrice);
        show.setPremiumPrice(premiumPrice);
        show.setReclinerPrice(reclinerPrice);
    }

    // Reschedule show (only allowed if show has NO bookings)
    public void updateShowSchedule(
            String role,
            int showId,
            LocalDate releaseDate,
            String newScreenId,
            LocalDate newShowDate,
            LocalTime newStartTime,
            int newDuration) {

        validateRole(role);

        Show show = getShowById(showId);

        if ("CANCELLED".equalsIgnoreCase(show.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot update a cancelled show");
        }

        // Once a show has bookings, screen and schedule must not be changed
        if (hasBookings(showId)) {
            throw new IllegalArgumentException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }

        if (newScreenId == null || newScreenId.trim().isEmpty()) {
            throw new IllegalArgumentException("Screen ID is required");
        }

        if (newShowDate == null || newStartTime == null) {
            throw new IllegalArgumentException("Date and time cannot be empty");
        }

        if (newDuration <= 0) {
            throw new IllegalArgumentException("Movie duration must be positive");
        }

        if (releaseDate != null && newShowDate.isBefore(releaseDate)) {
            throw new IllegalArgumentException(
                    "Show cannot be scheduled before movie release date");
        }

        LocalDateTime newStart = LocalDateTime.of(newShowDate, newStartTime);
        if (newStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Cannot create show for a past date or time");
        }

        LocalDateTime newEnd = newStart.plusMinutes(newDuration);
        checkScheduleAvailability(newScreenId, newStart, newEnd, showId);

        show.setScreenId(newScreenId);
        show.setShowDate(newShowDate);
        show.setStartTime(newStartTime);
        show.setMovieDuration(newDuration);
    }

    // Update movie (only allowed if show has NO bookings)
    public void updateMovie(String role, int showId, String newMovieId, LocalDate releaseDate) {

        validateRole(role);

        Show show = getShowById(showId);

        if ("CANCELLED".equalsIgnoreCase(show.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot update a cancelled show");
        }

        if (hasBookings(showId)) {
            throw new IllegalArgumentException(
                    "Once a show has bookings, movie, screen and start time must not be changed");
        }

        if (newMovieId == null || newMovieId.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie ID is required");
        }

        if (releaseDate != null && show.getShowDate().isBefore(releaseDate)) {
            throw new IllegalArgumentException(
                    "Show cannot be scheduled before movie release date");
        }

        show.setMovieId(newMovieId);
    }

    // Cancel a show - Admin only
    public void cancelShow(String role, int showId) {

        if (role == null
                || !role.trim().equalsIgnoreCase("ADMIN")) {

            throw new IllegalArgumentException(
                    "Only Admin can cancel a show");
        }

        Show show = getShowById(showId);

        if ("CANCELLED".equalsIgnoreCase(show.getStatus())) {
            throw new IllegalArgumentException(
                    "Show is already cancelled");
        }

        show.setStatus("CANCELLED");

        // Cancel all associated bookings and initiate full refunds, including convenience fees
        if (bookingService != null) {
            List<Booking> bookings = bookingService.getBookingsByShowId(showId);
            for (Booking booking : bookings) {
                if (!"CANCELLED".equalsIgnoreCase(booking.getStatus())) {
                    booking.setStatus("CANCELLED");
                    if (refundService != null) {
                        refundService.processRefund(booking, "Show cancelled by Admin");
                    }
                }
            }
        }
        show.cancelAllBookings();
    }
}
