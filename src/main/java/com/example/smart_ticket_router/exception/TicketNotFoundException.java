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

}