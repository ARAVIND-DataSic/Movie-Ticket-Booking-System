package com.movie.repository;
import com.movie.models.users.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    User save(User user);
    class InMemory implements UserRepository {
        private final Map<String,User> users = new ConcurrentHashMap<>();
        public Optional<User> findByEmail(String email) { return users.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst(); }
        public Optional<User> findById(String id) { return Optional.ofNullable(users.get(id)); }
        public User save(User user) { users.put(user.getId(), user); return user; }
    }
}
