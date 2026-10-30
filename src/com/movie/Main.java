
package com.movie;

import com.movie.CLI.MovieCLI;
import com.movie.CLI.ShowCLI;
import com.movie.CLI.TheatreCLI;
import com.movie.repository.BookingRepository;
import com.movie.repository.InMemoryMovieRepository;
import com.movie.repository.InMemoryScreenRepository;
import com.movie.repository.InMemorySeatRepository;
import com.movie.repository.InMemoryTheatreRepository;
import com.movie.repository.ShowRepository;
import com.movie.services.BookingService;
import com.movie.services.MovieService;
import com.movie.services.RefundService;
import com.movie.services.ScreenService;
import com.movie.services.SeatService;
import com.movie.services.ShowService;
import com.movie.services.TheatreService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // --- Movie & Theatre module (Member 1/2) ---
        InMemoryMovieRepository movieRepository = new InMemoryMovieRepository();
        InMemoryTheatreRepository theatreRepository = new InMemoryTheatreRepository();
        InMemoryScreenRepository screenRepository = new InMemoryScreenRepository();
        InMemorySeatRepository seatRepository = new InMemorySeatRepository();

        MovieCLI movieCLI = new MovieCLI(new MovieService(movieRepository));
        TheatreCLI theatreCLI = new TheatreCLI(
                new TheatreService(theatreRepository, screenRepository),
                new ScreenService(screenRepository, theatreRepository, seatRepository),
                new SeatService(seatRepository, screenRepository));

        // --- Show Scheduling module (Member 3) ---
        ShowRepository showRepository = new ShowRepository();
        BookingService bookingService = new BookingService();
        RefundService refundService = new RefundService();
        ShowService showService = new ShowService(showRepository, bookingService, refundService);
        ShowCLI showCLI = new ShowCLI(showService);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running && scanner.hasNextLine()) {
                System.out.println("\n=== Movie Ticket Booking System ===");
                System.out.println("1. Movie management");
                System.out.println("2. Theatre, screen & seat management");
                System.out.println("3. Show scheduling");
                System.out.println("0. Exit");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        movieCLI.run(scanner);
                        break;
                    case "2":
                        theatreCLI.run(scanner);
                        break;
                    case "3":
                        showCLI.start();
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Choose 0, 1, 2, or 3.");
                }
            }
        }
    }
}
