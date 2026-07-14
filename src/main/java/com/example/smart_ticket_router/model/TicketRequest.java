package com.example.smart_ticket_router.model;

import jakarta.validation.constraints.NotBlank;

public class TicketRequest {

    @NotBlank(message = "Ticket message cannot be empty")
    private String message;

    public TicketRequest() {
    }

    public TicketRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}