package com.movie.models.theatre;

public class Screen {

    private String screenId;
    private String theatreId;
    private String name;
    private int totalSeats;

    public Screen() {
    }

    public Screen(String screenId, String theatreId, String name, int totalSeats) {
        this.screenId = screenId;
        this.theatreId = theatreId;
        this.name = name;
        this.totalSeats = totalSeats;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}
