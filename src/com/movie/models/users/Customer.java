package com.movie.models.users;

public class Customer extends User {

    public Customer() {
        setRole("CUSTOMER");
    }

    public Customer(String userId, String name) {
        super(userId, name, "CUSTOMER");
    }
}
