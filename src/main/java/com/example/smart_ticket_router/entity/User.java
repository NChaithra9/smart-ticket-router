package com.example.smart_ticket_router.entity;

import com.example.smart_ticket_router.enums.Role;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an application user.
 *
 * <p>A user can authenticate into the application and submit
 * support tickets. Each user is assigned a role that determines
 * the level of access within the application.
 *
 * <p>A user can have multiple support tickets, represented by
 * a one-to-many relationship with the {@link Ticket} entity.
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
     * Must be unique.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role assigned to the user.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}