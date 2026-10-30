package com.movie.models.users;
public class Customer extends User {
    public Customer(String name, String email, String password) { super(name, email, password); }
    public Customer(String id, String name, String email, String password) { super(id, name, email, password); }
}
