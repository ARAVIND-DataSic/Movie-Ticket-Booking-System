package com.movie.CLI;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

final class ConsoleInput {
    private ConsoleInput() { }

    static String text(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }

    static int positiveInt(Scanner scanner, String prompt) {
        String value = text(scanner, prompt);
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) throw new IllegalArgumentException("Enter a number greater than zero.");
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid whole number.");
        }
    }

    static LocalDate date(Scanner scanner, String prompt) {
        String value = text(scanner, prompt);
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter a date in YYYY-MM-DD format.");
        }
    }

    static <T extends Enum<T>> T enumeration(Scanner scanner, String prompt, Class<T> enumType) {
        String value = text(scanner, prompt).trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid value. Choose one of: " + enumValues(enumType));
        }
    }

    private static <T extends Enum<T>> String enumValues(Class<T> enumType) {
        StringBuilder values = new StringBuilder();
        for (T value : enumType.getEnumConstants()) {
            if (values.length() > 0) values.append(", ");
            values.append(value.name());
        }
        return values.toString();
    }
}
