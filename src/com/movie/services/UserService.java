package com.movie.services;
import com.movie.models.users.User;
import com.movie.repository.UserRepository;
import java.util.regex.Pattern;
public class UserService {
    private final UserRepository users;
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    public UserService(UserRepository users) { this.users = users; }
    public User register(String name,String email,String password) {
        if (name==null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email==null || !EMAIL.matcher(email.trim()).matches()) throw new IllegalArgumentException("Enter a valid email");
        if (password==null || password.length()<8) throw new IllegalArgumentException("Password must have at least 8 characters");
        String normalized=email.trim().toLowerCase();
        if (users.findByEmail(normalized).isPresent()) throw new IllegalArgumentException("Email is already registered");
        return users.save(new User(name.trim(),normalized,password));
    }
}
