package com.movie.models.users;

import java.util.UUID;

/**
 * Unified User model.
 *
 * Preserves Member 1's immutable id/email/password design while also
 * exposing the mutable userId/role accessors required by Member 3's
 * Show Scheduling role-based authorisation.
 *
 * Constructor disambiguation:
 *   Member 1 – User(name, email, password)          3-arg: name, email, password
 *   Member 1 – User(id, name, email, password)      4-arg: all four fields
 *   Member 3 – User()                               no-arg
 *   Member 3 – subclass calls super via 4-arg with email=null, password=null
 *              and uses setRole() after construction.
 */
public class User {

    // ── Member 1 fields ───────────────────────────────────────────────────────
    private final String id;
    private final String name;
    private final String email;
    private final String password;

    // ── Member 3 fields ───────────────────────────────────────────────────────
    private String userId;
    private String role;

    // =========================================================================
    // Member 1 constructors
    // =========================================================================
    public User(String name, String email, String password) {
        this(UUID.randomUUID().toString(), name, email, password);
    }

    public User(String id, String name, String email, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.userId   = id;
    }

    // =========================================================================
    // Member 3 constructors
    // =========================================================================
    public User() {
        this.id       = UUID.randomUUID().toString();
        this.name     = null;
        this.email    = null;
        this.password = null;
        this.userId   = this.id;
    }

    // =========================================================================
    // Member 1 accessors
    // =========================================================================
    /** Primary ID accessor (Member 1). */
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    // =========================================================================
    // Member 3 accessors
    // =========================================================================
    /** Alias for {@link #getId()} used by Member 3. */
    public String getUserId()               { return userId != null ? userId : id; }
    public void setUserId(String userId)    { this.userId = userId; }

    public void setName(String name) {
        // name is final; setter exists for interface compatibility only.
    }

    public String getRole()                 { return role; }
    public void setRole(String role)        { this.role = role; }
}
