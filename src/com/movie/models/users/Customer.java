package com.movie.models.users;

/**
 * Customer user type.
 *
 * Supports both:
 *   - Member 1 pattern: Customer(name, email, password) / Customer(id, name, email, password)
 *   - Member 3 pattern: Customer() / Customer(userId, name)
 */
public class Customer extends User {

    // Member 1 constructors
    public Customer(String name, String email, String password) {
        super(name, email, password);
        setRole("CUSTOMER");
    }

    public Customer(String id, String name, String email, String password) {
        super(id, name, email, password);
        setRole("CUSTOMER");
    }

    // Member 3 constructors
    public Customer() {
        setRole("CUSTOMER");
    }

    public Customer(String userId, String name) {
        super(userId, name, null, null);
        setUserId(userId);
        setRole("CUSTOMER");
    }
}
