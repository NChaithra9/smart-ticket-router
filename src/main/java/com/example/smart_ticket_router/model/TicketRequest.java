package com.example.smart_ticket_router.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents the request payload for creating a support ticket.
 * <p>
 * This class contains the ticket message submitted by the user.
 * The message must not be blank.
 * </p>
 */
public class TicketRequest {

    /**
     * Logger for TicketRequest.
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketRequest.class);

    /**
     * Description of the support issue.
     */
    @NotBlank(message = "Ticket message cannot be empty")
    private String message;

    /**
     * Default constructor.
     */
    public TicketRequest() {
        logger.debug("TicketRequest object created.");
    }

    /**
     * Parameterized constructor.
     *
     * @param message the support ticket message
     */
    public TicketRequest(String message) {
        logger.debug("TicketRequest object created with message.");
        this.message = message;
    }

    /**
     * Returns the ticket message.
     *
     * @return the support ticket message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the ticket message.
     *
     * @param message the support ticket message
     */
    public void setMessage(String message) {
        logger.debug("Setting ticket message.");
        this.message = message;
    }
}