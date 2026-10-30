package com.movie.models.users;

public class TheatreManager extends User {

    public TheatreManager() {
        setRole("THEATRE_MANAGER");
    }

    public TheatreManager(String userId, String name) {
        super(userId, name, "THEATRE_MANAGER");
    }
}
