package com.example.smart_ticket_router.exception;

import java.time.LocalDateTime;

/**
 * Represents a standardized error response returned
 * to clients when an exception occurs.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Default constructor.
     */
    public ErrorResponse() {
    }

    /**
     * Constructs an ErrorResponse.
     *
     * @param timestamp time when the error occurred
     * @param status HTTP status code
     * @param error HTTP error description
     * @param message detailed error message
     * @param path request path
     */
    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path) {

        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}