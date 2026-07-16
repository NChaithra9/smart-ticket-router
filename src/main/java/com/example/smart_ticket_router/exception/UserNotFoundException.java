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

    /**
     * Constructs the exception with an underlying cause.
     *
     * @param message exception message
     * @param cause the original exception that triggered this failure
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}