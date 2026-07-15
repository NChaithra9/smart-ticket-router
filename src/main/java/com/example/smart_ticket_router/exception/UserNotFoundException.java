package com.example.smart_ticket_router.exception;

/**
 * Exception thrown when a user
 * cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs the exception.
     *
     * @param message exception message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}