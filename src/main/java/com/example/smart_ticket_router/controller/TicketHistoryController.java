package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for displaying the ticket history
 * of the currently authenticated user.
 *
 * <p>This controller retrieves the logged-in user's details
 * from the Spring Security context and fetches all tickets
 * associated with that user.
 */
@Controller
public class TicketHistoryController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketHistoryController.class);

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a TicketHistoryController.
     *
     * @param ticketRepository repository used to retrieve tickets
     * @param userRepository repository used to retrieve user information
     */
    public TicketHistoryController(
            TicketRepository ticketRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Displays the ticket history of the authenticated user.
     *
     * @param model Spring MVC model used to pass ticket data to the view
     * @return the ticket history view
     */
    @GetMapping("/my-tickets")
    public String myTickets(Model model) {

        logger.info("Fetching ticket history for the authenticated user.");

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Authenticated user: {}", authentication.getName());

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        var tickets = ticketRepository.findByUser(user);

        logger.debug("Retrieved {} tickets for user {}.",
                tickets.size(), user.getEmail());

        model.addAttribute("tickets", tickets);

        logger.info("Rendering ticket history page.");

        return "history";
    }
}