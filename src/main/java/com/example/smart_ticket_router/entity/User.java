package com.example.smart_ticket_router.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing an application user.
 *
 * <p>
 * A user can authenticate into the application and submit
 * support tickets. Each user can be assigned one or more roles,
 * and each role determines the permissions available to the user.
 * </p>
 *
 * <p>
 * A user can also have multiple support tickets represented by
 * a one-to-many relationship with the {@link Ticket} entity.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email address used for authentication.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Roles assigned to the user.
     *
     * <p>
     * A user may possess multiple roles,
     * and each role grants one or more permissions.
     * </p>
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Support tickets submitted by the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Returns the user ID.
     *
     * @return user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id user ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user's full name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's full name.
     *
     * @param name user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's email address.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the encrypted password.
     *
     * @return encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encrypted password.
     *
     * @param password encoded password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns all roles assigned to the user.
     *
     * @return assigned roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Assigns roles to the user.
     *
     * @param roles application roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Returns all tickets submitted by the user.
     *
     * @return support tickets
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * Sets the tickets submitted by the user.
     *
     * @param tickets support tickets
     */
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}