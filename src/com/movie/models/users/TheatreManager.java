package com.movie.models.users;

/**
 * TheatreManager user type.
 *
 * Supports both:
 *   - Member 3 pattern: TheatreManager() / TheatreManager(userId, name)
 */
public class TheatreManager extends User {

    // Member 3 constructors
    public TheatreManager() {
        setRole("THEATRE_MANAGER");
    }

    public TheatreManager(String userId, String name) {
        super(userId, name, null, null);
        setUserId(userId);
        setRole("THEATRE_MANAGER");
    }
}
