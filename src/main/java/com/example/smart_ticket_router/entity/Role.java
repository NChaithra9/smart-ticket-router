package com.example.smart_ticket_router.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a role within the application.
 *
 * <p>
 * A role groups together multiple permissions and
 * can be assigned to multiple users.
 * </p>
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role.
     *
     * Examples:
     * ROLE_ADMIN
     * ROLE_USER
     */
    @Column(nullable = false, unique = true)
    private String roleName;

    /**
     * Permissions assigned to this role.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Users assigned to this role.
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * Default constructor.
     */
    public Role() {
    }

    /**
     * Creates a role with the given name.
     *
     * @param roleName role name
     */
    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}