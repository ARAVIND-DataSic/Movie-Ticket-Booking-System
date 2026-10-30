package com.movie.models.theatre;

public class Screen {
    private final String id;
    private final String theatreId;
    private String name;
    private int seatCapacity;

    public Screen(String id, String theatreId, String name, int seatCapacity) {
        this.id = requireText(id, "id");
        this.theatreId = requireText(theatreId, "theatreId");
        setName(name);
        setSeatCapacity(seatCapacity);
    }

    public String getId() { return id; }
    public String getTheatreId() { return theatreId; }
    public String getName() { return name; }
    public int getSeatCapacity() { return seatCapacity; }

    public void setName(String name) { this.name = requireText(name, "name"); }
    public void setSeatCapacity(int seatCapacity) {
        if (seatCapacity <= 0) throw new IllegalArgumentException("Seat capacity must be greater than zero.");
        this.seatCapacity = seatCapacity;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | " + name + " | theatre " + theatreId + " | capacity " + seatCapacity;
    }
}
