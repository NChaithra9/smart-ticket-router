package com.example.smart_ticket_router.exception;

/**
 * Exception thrown when attempting to register
 * a user with an email address that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs the exception.
     *
     * @param message exception message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

}