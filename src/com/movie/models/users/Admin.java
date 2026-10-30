package com.movie.models.users;

public class Admin extends User {

    public Admin() {
        setRole("ADMIN");
    }

    public Admin(String userId, String name) {
        super(userId, name, "ADMIN");
    }
}
