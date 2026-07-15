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

}