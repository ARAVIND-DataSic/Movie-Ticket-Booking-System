package com.movie.models.movie;

import java.time.LocalDate;
import java.util.Objects;

public class Movie {
    private final String id;
    private String title;
    private String description;
    private int durationMinutes;
    private Genre genre;
    private Certificate certificate;
    private String language;
    private LocalDate releaseDate;

    public Movie(String id, String title, String description, int durationMinutes,
                 Genre genre, Certificate certificate, String language, LocalDate releaseDate) {
        this.id = requireText(id, "id");
        setTitle(title);
        setDescription(description);
        setDurationMinutes(durationMinutes);
        setGenre(genre);
        setCertificate(certificate);
        setLanguage(language);
        setReleaseDate(releaseDate);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public Genre getGenre() { return genre; }
    public Certificate getCertificate() { return certificate; }
    public String getLanguage() { return language; }
    public LocalDate getReleaseDate() { return releaseDate; }

    public void setTitle(String title) { this.title = requireText(title, "title"); }
    public void setDescription(String description) { this.description = description == null ? "" : description.trim(); }
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be greater than zero.");
        this.durationMinutes = durationMinutes;
    }
    public void setGenre(Genre genre) { this.genre = Objects.requireNonNull(genre, "genre"); }
    public void setCertificate(Certificate certificate) {
        this.certificate = Objects.requireNonNull(certificate, "certificate");
    }
    public void setLanguage(String language) { this.language = requireText(language, "language"); }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = Objects.requireNonNull(releaseDate, "releaseDate"); }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | " + title + " | " + durationMinutes + " min | " + genre
                + " | " + certificate + " | " + language + " | " + releaseDate;
    }
}
