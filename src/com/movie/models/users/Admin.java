package com.movie.models.users;

/**
 * Admin user type.
 *
 * Supports both:
 *   - Member 3 pattern: Admin() / Admin(userId, name)
 */
public class Admin extends User {

    // Member 3 constructors
    public Admin() {
        setRole("ADMIN");
    }

    public Admin(String userId, String name) {
        super(userId, name, null, null);
        setUserId(userId);
        setRole("ADMIN");
    }
}
