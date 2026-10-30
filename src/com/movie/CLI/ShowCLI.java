package com.movie.CLI;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import com.movie.models.show.Show;
import com.movie.repository.ShowRepository;
import com.movie.services.ShowService;

public class ShowCLI {

    private Scanner scanner;
    private ShowService showService;
    private String role;

    public ShowCLI() {
        this(new ShowService(new ShowRepository()), new Scanner(System.in));
    }

    public ShowCLI(ShowService showService) {
        this(showService, new Scanner(System.in));
    }

    public ShowCLI(ShowService showService, Scanner scanner) {
        this.showService = showService;
        this.scanner = scanner;
    }

    public void start() {

        System.out.println("\n==============================");
        System.out.println("     SHOW SCHEDULING SYSTEM");
        System.out.println("==============================");

        promptRole();

        while (true) {

            System.out.println("\n========== SHOW MENU ==========");
            System.out.println("Current Role: " + (role == null || role.isEmpty() ? "None" : role));
            System.out.println("1. Create Show");
            System.out.println("2. View All Shows");
            System.out.println("3. Search Show");
            System.out.println("4. Update Ticket Prices");
            System.out.println("5. Cancel Show");
            System.out.println("6. Reschedule Show Details");
            System.out.println("7. Switch Role");
            System.out.println("0. Exit");
            System.out.println("==============================");

            System.out.print("Enter your choice: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1":
                        createShow();
                        break;

                    case "2":
                        viewAllShows();
                        break;

                    case "3":
                        searchShow();
                        break;

                    case "4":
                        updatePrices();
                        break;

                    case "5":
                        cancelShow();
                        break;

                    case "6":
                        rescheduleShow();
                        break;

                    case "7":
                        promptRole();
                        break;

                    case "0":
                        System.out.println("Thank you!");
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void promptRole() {
        System.out.print("Enter your role (ADMIN / THEATRE_MANAGER): ");
        if (scanner.hasNextLine()) {
            role = scanner.nextLine().trim();
        }
    }

    // Create Show
    private void createShow() {

        System.out.println("\n--- CREATE SHOW ---");

        System.out.print("Enter Show ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Movie ID: ");
        String movieId = scanner.nextLine().trim();

        System.out.print("Enter Movie Release Date (YYYY-MM-DD): ");
        LocalDate releaseDate =
                LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter Screen ID: ");
        String screenId = scanner.nextLine().trim();

        System.out.print("Enter Show Date (YYYY-MM-DD): ");
        LocalDate showDate =
                LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter Start Time (HH:MM): ");
        LocalTime startTime =
                LocalTime.parse(scanner.nextLine().trim());

        System.out.print("Enter Movie Duration (minutes): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Regular Ticket Price: ");
        double regular = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Premium Ticket Price: ");
        double premium = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Recliner Ticket Price: ");
        double recliner = Double.parseDouble(scanner.nextLine().trim());

        Show show = showService.createShow(
                role,
                id,
                movieId,
                releaseDate,
                screenId,
                showDate,
                startTime,
                duration,
                regular,
                premium,
                recliner
        );

        System.out.println("\nShow created successfully!");
        displayShow(show);
    }

    // View All Shows
    private void viewAllShows() {

        System.out.println("\n--- ALL SHOWS ---");

        List<Show> shows = showService.getAllShows();

        if (shows.isEmpty()) {
            System.out.println("No shows available.");
            return;
        }

        for (Show show : shows) {
            displayShow(show);
        }
    }

    // Search Show
    private void searchShow() {

        System.out.print("Enter Show ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Show show = showService.getShowById(id);

        displayShow(show);
    }

    // Update Ticket Prices
    private void updatePrices() {

        System.out.println("\n--- UPDATE TICKET PRICES ---");

        System.out.print("Enter Show ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("New Regular Price: ");
        double regular = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("New Premium Price: ");
        double premium = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("New Recliner Price: ");
        double recliner = Double.parseDouble(scanner.nextLine().trim());

        showService.updateShowPrices(
                role, id, regular, premium, recliner);

        System.out.println("Ticket prices updated successfully!");
    }

    // Reschedule Show Details (Screen, Date, Time, Duration)
    private void rescheduleShow() {

        System.out.println("\n--- RESCHEDULE SHOW ---");

        System.out.print("Enter Show ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter New Screen ID: ");
        String screenId = scanner.nextLine().trim();

        System.out.print("Enter New Show Date (YYYY-MM-DD): ");
        LocalDate showDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Enter New Start Time (HH:MM): ");
        LocalTime startTime = LocalTime.parse(scanner.nextLine().trim());

        System.out.print("Enter Movie Duration (minutes): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        // Release date check using show's current date or release date
        showService.updateShowSchedule(
                role, id, null, screenId, showDate, startTime, duration);

        System.out.println("Show rescheduled successfully!");
        displayShow(showService.getShowById(id));
    }

    // Cancel Show
    private void cancelShow() {

        System.out.println("\n--- CANCEL SHOW ---");

        System.out.print("Enter Show ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        showService.cancelShow(role, id);

        System.out.println("Show cancelled successfully!");
    }

    // Display Show Details
    private void displayShow(Show show) {

        System.out.println("\n------------------------------");
        System.out.println("Show ID       : " + show.getShowId());
        System.out.println("Movie ID      : " + show.getMovieId());
        System.out.println("Screen ID     : " + show.getScreenId());
        System.out.println("Show Date     : " + show.getShowDate());
        System.out.println("Start Time    : " + show.getStartTime());
        System.out.println("End Time      : " + show.getEndTime());
        System.out.println("Duration      : " + show.getMovieDuration() + " mins");
        System.out.println("Regular Price : " + show.getRegularPrice());
        System.out.println("Premium Price : " + show.getPremiumPrice());
        System.out.println("Recliner Price: " + show.getReclinerPrice());
        System.out.println("Status        : " + show.getStatus());
        System.out.println("Bookings      : " + (show.hasBookings() ? show.getBookingCount() + " booked" : "None"));
        System.out.println("------------------------------");
    }
}
