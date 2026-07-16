package com.example.smart_ticket_router.exception;

/**
 * Exception thrown when
 * a support ticket is not found.
 */
public class TicketNotFoundException extends RuntimeException {

    /**
     * Constructs the exception.
     *
     * @param message exception message
     */
    public TicketNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with an underlying cause.
     *
     * @param message exception message
     * @param cause the original exception that triggered this failure
     */
    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}