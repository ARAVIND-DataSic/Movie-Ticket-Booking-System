package com.movie.models.theatre;

/**
 * Unified Screen model.
 *
 * Preserves the Member 1/2 implementation (final id, seatCapacity,
 * validation) while also exposing getScreenId() / getTotalSeats()
 * aliases needed by the Member 3 Show Scheduling module.
 */
public class Screen {

    // ── Member 1/2 fields ─────────────────────────────────────────────────────
    private final String id;
    private final String theatreId;
    private String name;
    private int seatCapacity;

    // =========================================================================
    // Member 1/2 constructor  (full, validated)
    // =========================================================================
    public Screen(String id, String theatreId, String name, int seatCapacity) {
        this.id = requireText(id, "id");
        this.theatreId = requireText(theatreId, "theatreId");
        setName(name);
        setSeatCapacity(seatCapacity);
    }

    // =========================================================================
    // Member 3 convenience constructors
    // =========================================================================
    public Screen() {
        this.id = null;
        this.theatreId = null;
    }

    /**
     * 4-arg constructor used by Member 3 (screenId, theatreId, name, totalSeats).
     * Delegates to the primary constructor.
     */
    public Screen(String screenId, String theatreId, String name, int totalSeats, boolean member3Ctor) {
        this.id = screenId;
        this.theatreId = theatreId;
        this.name = name;
        this.seatCapacity = totalSeats;
    }

    // =========================================================================
    // Member 1/2 accessors
    // =========================================================================
    /** Primary ID accessor (Member 1/2). */
    public String getId()                           { return id; }
    public String getTheatreId()                    { return theatreId; }
    public String getName()                         { return name; }
    public int getSeatCapacity()                    { return seatCapacity; }

    public void setName(String name)                { this.name = requireText(name, "name"); }
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

    // =========================================================================
    // Member 3 alias accessors
    // =========================================================================
    /**
     * Alias for {@link #getId()} used by the Show Scheduling module (Member 3).
     */
    public String getScreenId()                     { return id; }

    /**
     * Alias for {@link #getSeatCapacity()} used by the Show Scheduling module.
     */
    public int getTotalSeats()                      { return seatCapacity; }

    /** No-op setter for interface compatibility with Member 3. */
    public void setScreenId(String screenId) {
        // id is final; this setter exists for compatibility only.
    }

    /** Delegates to {@link #setSeatCapacity(int)} for compatibility. */
    public void setTotalSeats(int totalSeats)       { setSeatCapacity(totalSeats); }

    /** No-op setter for theatreId compatibility with Member 3. */
    public void setTheatreId(String theatreId) {
        // theatreId is final; this setter exists for compatibility only.
    }
}
