package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for routing support tickets.
 *
 * <p>This controller exposes REST endpoints for submitting support
 * tickets to the AI-powered ticket routing service. The ticket is
 * classified into a category, priority, assigned team, and reasoning
 * before being returned to the client.
 */
@RestController
@RequestMapping("/api")
public class TicketApiController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketApiController.class);

    private final TicketRoutingService ticketRoutingService;

    /**
     * Constructs a TicketApiController.
     *
     * @param ticketRoutingService service responsible for AI-based
     *                             ticket routing
     */
    public TicketApiController(TicketRoutingService ticketRoutingService) {
        this.ticketRoutingService = ticketRoutingService;
    }

    /**
     * Routes a support ticket using the AI routing service.
     *
     * @param request request containing the ticket message
     * @return AI-generated ticket classification
     */
    @PostMapping("/route")
    public TicketResponse routeTicket(@RequestBody TicketRequest request) {

        logger.info("Received API request to route a support ticket.");

        TicketResponse response =
                ticketRoutingService.routeTicket(request.getMessage());

        logger.info("Support ticket routed successfully.");

        return response;
    }
}