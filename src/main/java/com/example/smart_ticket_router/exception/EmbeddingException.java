package com.example.smart_ticket_router.exception;

/**
 * Exception thrown when an error occurs
 * while generating or retrieving embeddings.
 */
public class EmbeddingException extends RuntimeException {

    /**
     * Constructs the exception.
     *
     * @param message exception message
     */
    public EmbeddingException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with an underlying cause.
     *
     * @param message exception message
     * @param cause the original exception that triggered this failure
     */
    public EmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }

}