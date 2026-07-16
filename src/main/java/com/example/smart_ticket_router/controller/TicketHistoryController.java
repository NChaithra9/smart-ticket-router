package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.exception.UserNotFoundException;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for displaying the ticket history
 * of the currently authenticated user.
 *
 * <p>
 * Retrieves the logged-in user's details from the Spring
 * Security context and displays all tickets submitted
 * by that user.
 * </p>
 */
@Controller
public class TicketHistoryController {

    /**
     * Logger for TicketHistoryController.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TicketHistoryController.class);

    /**
     * Repository for ticket operations.
     */
    private final TicketRepository ticketRepository;

    /**
     * Repository for user operations.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a TicketHistoryController.
     *
     * @param ticketRepository repository for ticket operations
     * @param userRepository repository for user operations
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
     * <p>
     * Only users having the {@code VIEW_HISTORY}
     * permission are allowed to access this endpoint.
     * </p>
     *
     * @param model Spring MVC model
     * @return history page
     */
    @PreAuthorize("hasAuthority('VIEW_HISTORY')")
    @GetMapping("/my-tickets")
    public String myTickets(Model model) {

        logger.info("Fetching ticket history for authenticated user.");

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Authenticated user: {}",
                authentication.getName());

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> {

                    logger.error(
                            "Authenticated user '{}' not found in database.",
                            authentication.getName());

                    return new UserNotFoundException(
                            "Authenticated user not found: "
                                    + authentication.getName());
                });

        var tickets = ticketRepository.findByUser(user);

        logger.debug(
                "Retrieved {} tickets for user {}.",
                tickets.size(),
                user.getEmail());

        model.addAttribute("tickets", tickets);

        logger.info("Rendering ticket history page.");

        return "history";
    }
}