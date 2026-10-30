package com.movie.models.movie;

import java.time.LocalDate;

public class Movie {

    private String movieId;
    private String title;
    private int durationMinutes;
    private LocalDate releaseDate;

    public Movie() {
    }

    public Movie(String movieId, String title, int durationMinutes, LocalDate releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
