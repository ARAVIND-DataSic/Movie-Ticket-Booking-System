package com.movie.CLI;

import com.movie.models.movie.Certificate;
import com.movie.models.movie.Genre;
import com.movie.models.movie.Movie;
import com.movie.services.MovieService;
import java.time.LocalDate;
import java.util.Scanner;

public class MovieCLI {
    private final MovieService movieService;

    public MovieCLI(MovieService movieService) {
        this.movieService = movieService;
    }

    public void run(Scanner scanner) {
        boolean running = true;
        while (running && scanner.hasNextLine()) {
            System.out.println("\n--- Movie Management ---");
            System.out.println("1. List movies");
            System.out.println("2. Add or update movie");
            System.out.println("3. Delete movie");
            System.out.println("0. Back");
            String choice = ConsoleInput.text(scanner, "Choose: ");
            try {
                switch (choice) {
                    case "1":
                        movieService.findAll().forEach(System.out::println);
                        break;
                    case "2":
                        saveMovie(scanner);
                        break;
                    case "3":
                        movieService.delete(ConsoleInput.text(scanner, "Movie ID: "));
                        System.out.println("Movie deleted.");
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Choose 0, 1, 2, or 3.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void saveMovie(Scanner scanner) {
        String id = ConsoleInput.text(scanner, "Movie ID: ");
        boolean updating = movieService.findById(id).isPresent();
        String title = ConsoleInput.text(scanner, "Title: ");
        String description = ConsoleInput.text(scanner, "Description: ");
        int duration = ConsoleInput.positiveInt(scanner, "Duration in minutes: ");
        Genre genre = ConsoleInput.enumeration(scanner, "Genre " + enumOptions(Genre.values()) + ": ", Genre.class);
        Certificate certificate = ConsoleInput.enumeration(
                scanner, "Certificate " + enumOptions(Certificate.values()) + ": ", Certificate.class);
        String language = ConsoleInput.text(scanner, "Language: ");
        LocalDate releaseDate = ConsoleInput.date(scanner, "Release date (YYYY-MM-DD): ");
        movieService.save(new Movie(id, title, description, duration, genre, certificate, language, releaseDate));
        System.out.println(updating ? "Movie updated." : "Movie added.");
    }

    private static String enumOptions(Enum<?>[] values) {
        StringBuilder options = new StringBuilder("[");
        for (Enum<?> value : values) {
            if (options.length() > 1) options.append(", ");
            options.append(value.name());
        }
        return options.append(']').toString();
    }
}
