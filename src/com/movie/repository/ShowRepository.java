package com.movie.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.movie.models.show.Show;

public class ShowRepository {

    private List<Show> shows = new ArrayList<>();

    // Add a new show
    public void addShow(Show show) {

        if (show == null) {
            throw new IllegalArgumentException(
                    "Show cannot be null");
        }

        if (getShowById(show.getShowId()) != null) {
            throw new IllegalArgumentException(
                    "Show ID already exists");
        }

        shows.add(show);
    }

    // Get all shows
    public List<Show> getAllShows() {

        return new ArrayList<>(shows);
    }

    // Find show using ID
    public Show getShowById(int showId) {

        for (Show show : shows) {

            if (show.getShowId() == showId) {
                return show;
            }
        }

        return null;
    }

    // Update an existing show
    public boolean updateShow(Show updatedShow) {

        if (updatedShow == null) {
            return false;
        }

        for (int i = 0; i < shows.size(); i++) {

            if (shows.get(i).getShowId()
                    == updatedShow.getShowId()) {

                shows.set(i, updatedShow);
                return true;
            }
        }

        return false;
    }

    // Get shows by screen and date
    public List<Show> getShowsByScreenAndDate(
            String screenId, LocalDate showDate) {

        List<Show> result = new ArrayList<>();

        for (Show show : shows) {

            if (show.getScreenId().equals(screenId)
                    && show.getShowDate().equals(showDate)) {

                result.add(show);
            }
        }

        return result;
    }

    // Remove show (for administrative cleanup / testing)
    public boolean removeShow(int showId) {
        for (int i = 0; i < shows.size(); i++) {
            if (shows.get(i).getShowId() == showId) {
                shows.remove(i);
                return true;
            }
        }
        return false;
    }
}
