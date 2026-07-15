package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.repository.UserRepository;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for handling the web interface for
 * submitting and routing support tickets.
 *
 * <p>This controller provides endpoints to:
 * <ul>
 *     <li>Display the ticket submission form.</li>
 *     <li>Route support tickets using the AI routing service.</li>
 *     <li>Persist routed tickets for the authenticated user.</li>
 * </ul>
 */
@Controller
public class TicketWebController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketWebController.class);

    private final TicketRoutingService ticketRoutingService;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    /**
     * Constructs a TicketWebController.
     *
     * @param ticketRoutingService service responsible for routing tickets
     * @param userRepository repository used to retrieve user information
     * @param ticketRepository repository used to persist tickets
     */
    public TicketWebController(
            TicketRoutingService ticketRoutingService,
            UserRepository userRepository,
            TicketRepository ticketRepository) {

        this.ticketRoutingService = ticketRoutingService;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Displays the ticket submission page.
     *
     * @param model Spring MVC model used to bind the ticket request
     * @return the home page
     */
    @GetMapping("/")
    public String home(Model model) {

        logger.info("Home page requested.");

        model.addAttribute("ticketRequest", new TicketRequest());

        return "index";
    }

    /**
     * Routes a submitted support ticket, associates it with the
     * authenticated user, stores it in the database, and displays
     * the routing result.
     *
     * @param ticketRequest support ticket submitted by the user
     * @param model Spring MVC model used to pass data to the view
     * @return the home page displaying the routing result
     */
    @PostMapping("/route")
    public String routeTicket(
            @ModelAttribute TicketRequest ticketRequest,
            Model model) {

        logger.info("Received support ticket for routing.");

        TicketResponse response =
                ticketRoutingService.routeTicket(ticketRequest.getMessage());

        logger.info("Ticket routed successfully.");

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Authenticated user: {}", authentication.getName());

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found."));

        Ticket ticket = new Ticket();

        ticket.setMessage(ticketRequest.getMessage());
        ticket.setCategory(response.getCategory());
        ticket.setPriority(response.getPriority());
        ticket.setAssignedTeam(response.getAssignedTeam());
        ticket.setReason(response.getReason());
        ticket.setUser(user);

        ticketRepository.save(ticket);

        logger.info("Ticket saved successfully for user: {}", email);

        model.addAttribute("ticketRequest", ticketRequest);
        model.addAttribute("response", response);

        logger.info("Rendering ticket routing result.");

        return "index";
    }
}