package com.movie.models.theatre;

import java.util.Objects;

public class Seat {
    private final String id;
    private final String screenId;
    private String rowLabel;
    private int seatNumber;
    private SeatType type;

    public Seat(String id, String screenId, String rowLabel, int seatNumber, SeatType type) {
        this.id = requireText(id, "id");
        this.screenId = requireText(screenId, "screenId");
        setRowLabel(rowLabel);
        setSeatNumber(seatNumber);
        setType(type);
    }

    public String getId() { return id; }
    public String getScreenId() { return screenId; }
    public String getRowLabel() { return rowLabel; }
    public int getSeatNumber() { return seatNumber; }
    public SeatType getType() { return type; }

    public void setRowLabel(String rowLabel) { this.rowLabel = requireText(rowLabel, "rowLabel").toUpperCase(); }
    public void setSeatNumber(int seatNumber) {
        if (seatNumber <= 0) throw new IllegalArgumentException("Seat number must be greater than zero.");
        this.seatNumber = seatNumber;
    }
    public void setType(SeatType type) { this.type = Objects.requireNonNull(type, "type"); }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return id + " | screen " + screenId + " | " + rowLabel + seatNumber + " | " + type;
    }
}
