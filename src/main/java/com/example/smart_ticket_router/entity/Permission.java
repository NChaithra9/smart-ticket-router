package com.example.smart_ticket_router.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an application permission.
 *
 * <p>
 * Permissions define individual actions that can be
 * granted to one or more roles.
 * </p>
 */
@Entity
@Table(name = "permissions")
public class Permission {

    /**
     * Unique identifier for the permission.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Permission name.
     *
     * Examples:
     * CREATE_TICKET
     * VIEW_ALL_TICKETS
     * DELETE_TICKET
     */
    @Column(nullable = false, unique = true)
    private String permissionName;

    /**
     * Roles that contain this permission.
     */
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    /**
     * Default constructor.
     */
    public Permission() {
    }

    /**
     * Creates a permission.
     *
     * @param permissionName permission name
     */
    public Permission(String permissionName) {
        this.permissionName = permissionName;
    }

    public Long getId() {
        return id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}