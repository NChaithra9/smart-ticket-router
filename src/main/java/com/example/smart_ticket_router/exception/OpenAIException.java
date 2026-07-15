package com.example.smart_ticket_router.exception;

/**
 * Exception thrown when communication
 * with the OpenAI API fails.
 */
public class OpenAIException extends RuntimeException {

    /**
     * Constructs the exception.
     *
     * @param message exception message
     */
    public OpenAIException(String message) {
        super(message);
    }

}