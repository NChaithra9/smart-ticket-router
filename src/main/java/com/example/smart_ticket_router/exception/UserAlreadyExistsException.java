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

    /**
     * Constructs the exception with an underlying cause.
     *
     * @param message exception message
     * @param cause the original exception that triggered this failure
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}