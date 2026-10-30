package com.movie.models.movie;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Unified Movie model.
 *
 * Preserves the full Member 1/2 implementation (id, description, genre,
 * certificate, language, validation helpers) while also exposing the
 * getMovieId() accessor needed by the Member 3 Show Scheduling module.
 */
public class Movie {

    // ── Member 1/2 fields ─────────────────────────────────────────────────────
    private final String id;
    private String title;
    private String description;
    private int durationMinutes;
    private Genre genre;
    private Certificate certificate;
    private String language;
    private LocalDate releaseDate;

    // =========================================================================
    // Member 1/2 constructor  (full, validated)
    // =========================================================================
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

    // =========================================================================
    // Member 3 convenience constructor  (minimal, no-arg + 4-arg)
    // =========================================================================
    public Movie() {
        this.id = null;
    }

    public Movie(String movieId, String title, int durationMinutes, LocalDate releaseDate) {
        this.id = movieId;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
    }

    // =========================================================================
    // Member 1/2 accessors
    // =========================================================================
    /** Primary ID accessor (Member 1/2). */
    public String getId()                                   { return id; }
    public String getTitle()                                { return title; }
    public String getDescription()                          { return description; }
    public int getDurationMinutes()                         { return durationMinutes; }
    public Genre getGenre()                                 { return genre; }
    public Certificate getCertificate()                     { return certificate; }
    public String getLanguage()                             { return language; }
    public LocalDate getReleaseDate()                       { return releaseDate; }

    public void setTitle(String title)                      { this.title = requireText(title, "title"); }
    public void setDescription(String description)          { this.description = description == null ? "" : description.trim(); }
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be greater than zero.");
        this.durationMinutes = durationMinutes;
    }
    public void setGenre(Genre genre)                       { this.genre = Objects.requireNonNull(genre, "genre"); }
    public void setCertificate(Certificate certificate)     { this.certificate = Objects.requireNonNull(certificate, "certificate"); }
    public void setLanguage(String language)                { this.language = requireText(language, "language"); }
    public void setReleaseDate(LocalDate releaseDate)       { this.releaseDate = Objects.requireNonNull(releaseDate, "releaseDate"); }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | " + title + " | " + durationMinutes + " min | " + genre
                + " | " + certificate + " | " + language + " | " + releaseDate;
    }

    // =========================================================================
    // Member 3 alias accessors
    // =========================================================================
    /**
     * Alias for {@link #getId()} used by the Show Scheduling module (Member 3).
     * Returns the movie's ID regardless of which constructor was used.
     */
    public String getMovieId()                              { return id; }

    /**
     * No-op setter provided for compatibility with Member 3 code that may call
     * setMovieId(). The id field is final; call this only on objects created
     * via the no-arg or 4-arg constructor where id was set at construction time.
     */
    public void setMovieId(String movieId) {
        // id is final – this setter exists for interface compatibility only.
        // Use the 4-arg constructor to set the id when creating a Movie for Member 3.
    }
}
