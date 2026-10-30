package com.movie.CLI;

import com.movie.models.theatre.Screen;
import com.movie.models.theatre.Seat;
import com.movie.models.theatre.SeatType;
import com.movie.models.theatre.Theatre;
import com.movie.services.ScreenService;
import com.movie.services.SeatService;
import com.movie.services.TheatreService;
import java.util.List;
import java.util.Scanner;

public class TheatreCLI {
    private final TheatreService theatreService;
    private final ScreenService screenService;
    private final SeatService seatService;

    public TheatreCLI(TheatreService theatreService, ScreenService screenService, SeatService seatService) {
        this.theatreService = theatreService;
        this.screenService = screenService;
        this.seatService = seatService;
    }

    public void run(Scanner scanner) {
        boolean running = true;
        while (running && scanner.hasNextLine()) {
            System.out.println("\n--- Theatre, Screen & Seat Management ---");
            System.out.println("1. List theatres");
            System.out.println("2. Add or update theatre");
            System.out.println("3. Delete theatre");
            System.out.println("4. List screens");
            System.out.println("5. Add or update screen");
            System.out.println("6. Delete screen");
            System.out.println("7. List seats for a screen");
            System.out.println("8. Add or update seat");
            System.out.println("9. Delete seat");
            System.out.println("0. Back");
            String choice = ConsoleInput.text(scanner, "Choose: ");
            try {
                switch (choice) {
                    case "1":
                        printList(theatreService.findAll());
                        break;
                    case "2":
                        saveTheatre(scanner);
                        break;
                    case "3":
                        theatreService.delete(ConsoleInput.text(scanner, "Theatre ID: "));
                        System.out.println("Theatre deleted.");
                        break;
                    case "4":
                        printList(screenService.findAll());
                        break;
                    case "5":
                        saveScreen(scanner);
                        break;
                    case "6":
                        screenService.delete(ConsoleInput.text(scanner, "Screen ID: "));
                        System.out.println("Screen deleted.");
                        break;
                    case "7":
                        printList(seatService.findByScreenId(ConsoleInput.text(scanner, "Screen ID: ")));
                        break;
                    case "8":
                        saveSeat(scanner);
                        break;
                    case "9":
                        seatService.delete(ConsoleInput.text(scanner, "Seat ID: "));
                        System.out.println("Seat deleted.");
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Choose a listed option.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void saveTheatre(Scanner scanner) {
        String id = ConsoleInput.text(scanner, "Theatre ID: ");
        boolean updating = theatreService.findById(id).isPresent();
        Theatre theatre = new Theatre(id,
                ConsoleInput.text(scanner, "Name: "),
                ConsoleInput.text(scanner, "Address: "),
                ConsoleInput.text(scanner, "City: "));
        theatreService.save(theatre);
        System.out.println(updating ? "Theatre updated." : "Theatre added.");
    }

    private void saveScreen(Scanner scanner) {
        String id = ConsoleInput.text(scanner, "Screen ID: ");
        boolean updating = screenService.findById(id).isPresent();
        Screen screen = new Screen(id,
                ConsoleInput.text(scanner, "Theatre ID: "),
                ConsoleInput.text(scanner, "Screen name: "),
                ConsoleInput.positiveInt(scanner, "Seat capacity: "));
        screenService.save(screen);
        System.out.println(updating ? "Screen updated." : "Screen added.");
    }

    private void saveSeat(Scanner scanner) {
        String id = ConsoleInput.text(scanner, "Seat ID: ");
        boolean updating = seatService.findById(id).isPresent();
        Seat seat = new Seat(id,
                ConsoleInput.text(scanner, "Screen ID: "),
                ConsoleInput.text(scanner, "Row label (e.g. A): "),
                ConsoleInput.positiveInt(scanner, "Seat number: "),
                ConsoleInput.enumeration(scanner, "Seat type " + enumOptions(SeatType.values()) + ": ", SeatType.class));
        seatService.save(seat);
        System.out.println(updating ? "Seat updated." : "Seat added.");
    }

    private static void printList(List<?> items) {
        if (items.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (Object item : items) System.out.println(item);
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
