package com.example.smart_ticket_router.enums;

/**
 * Represents the roles available in the application.
 *
 * <p>Roles determine the level of access granted to authenticated
 * users through Spring Security.
 */
public enum Role {

    /**
     * Standard application user.
     *
     * <p>Users with this role can submit support tickets and
     * view their own ticket history.
     */
    USER,

    /**
     * Administrator of the application.
     *
     * <p>Users with this role can access administrative features,
     * including viewing and managing all submitted tickets.
     */
    ADMIN
}