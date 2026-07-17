package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.exception.TicketNotFoundException;
import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST controller responsible for routing and retrieving support tickets.
 *
 * <p>This controller exposes REST endpoints for submitting support
 * tickets to the AI-powered ticket routing service. The ticket is
 * classified into a category, priority, assigned team, and reasoning
 * before being returned to the client. It also exposes a lookup
 * endpoint for retrieving a previously routed ticket by its ID.
 */
@RestController
@RequestMapping("/api")
public class TicketApiController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketApiController.class);

    private final TicketRoutingService ticketRoutingService;
    private final TicketRepository ticketRepository;

    /**
     * Constructs a TicketApiController.
     *
     * @param ticketRoutingService service responsible for AI-based
     *                             ticket routing
     * @param ticketRepository repository used to look up persisted tickets
     */
    public TicketApiController(TicketRoutingService ticketRoutingService,
                               TicketRepository ticketRepository) {
        this.ticketRoutingService = ticketRoutingService;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Routes a support ticket using the AI routing service.
     *
     * @param request request containing the ticket message
     * @return AI-generated ticket classification
     * @throws org.springframework.web.bind.MethodArgumentNotValidException
     *         if the ticket message is blank
     */
    @PreAuthorize("hasAuthority('CREATE_TICKET')")
    @PostMapping("/route")
    public TicketResponse routeTicket(@Valid @RequestBody TicketRequest request) {

        logger.info("Received API request to route a support ticket.");

        TicketResponse response =
                ticketRoutingService.routeTicket(request.getMessage());

        logger.info("Support ticket routed successfully.");

        return response;
    }

    /**
     * Retrieves a single support ticket by its identifier.
     *
     * @param id unique identifier of the ticket
     * @return the matching ticket
     * @throws TicketNotFoundException if no ticket exists with the
     *                                 given ID
     */
    @PreAuthorize("hasAuthority('VIEW_ALL_TICKETS')")
    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable Long id) {

        logger.info("Received API request to fetch ticket ID: {}", id);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Ticket not found. ID: {}", id);

                    return new TicketNotFoundException(
                            "Ticket not found with ID: " + id);
                });

        logger.info("Ticket ID {} retrieved successfully.", id);

        return ticket;
    }
}