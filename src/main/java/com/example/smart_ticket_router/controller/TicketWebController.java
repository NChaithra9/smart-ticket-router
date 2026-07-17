package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.exception.UserNotFoundException;
import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.UserRepository;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

/**
 * Controller responsible for handling the web interface for
 * submitting and routing support tickets.
 *
 * <p>This controller provides endpoints to:
 * <ul>
 *     <li>Display the ticket submission form.</li>
 *     <li>Route support tickets using the AI routing service, which
 *     persists the ticket and links it to the authenticated user in a
 *     single atomic operation.</li>
 * </ul>
 */
@Controller
public class TicketWebController {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketWebController.class);

    private final TicketRoutingService ticketRoutingService;
    private final UserRepository userRepository;

    /**
     * Constructs a TicketWebController.
     *
     * @param ticketRoutingService service responsible for routing,
     *                             persisting and associating tickets
     * @param userRepository repository used to retrieve user information
     */
    public TicketWebController(
            TicketRoutingService ticketRoutingService,
            UserRepository userRepository) {

        this.ticketRoutingService = ticketRoutingService;
        this.userRepository = userRepository;
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
     * authenticated user, persists it, and displays the routing result.
     *
     * <p>
     * The authenticated user is resolved first, then
     * {@link TicketRoutingService#routeTicket(String, User)} is called
     * once to classify and save the ticket already linked to that user,
     * so exactly one ticket row is created per submission.
     * </p>
     *
     * @param ticketRequest support ticket submitted by the user
     * @param bindingResult validation result for {@code ticketRequest}
     * @param model Spring MVC model used to pass data to the view
     * @return the home page displaying the routing result, or
     *         re-displaying the form with a validation error if the
     *         submitted message was blank
     * @throws UserNotFoundException if the authenticated user cannot be
     *                               found in the database
     */
    @PostMapping("/route")
    public String routeTicket(
            @Valid @ModelAttribute TicketRequest ticketRequest,
            BindingResult bindingResult,
            Model model) {

        logger.info("Received support ticket for routing.");

        if (bindingResult.hasErrors()) {

            logger.warn("Ticket submission rejected due to validation errors: {}",
                    bindingResult.getAllErrors());

            return "index";
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Authenticated user: {}", authentication.getName());

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.error(
                            "Authenticated user '{}' not found in database.",
                            email);

                    return new UserNotFoundException(
                            "Authenticated user not found: " + email);
                });

        TicketResponse response =
                ticketRoutingService.routeTicket(
                        ticketRequest.getMessage(), user);

        logger.info("Ticket routed and saved successfully for user: {}", email);

        model.addAttribute("ticketRequest", ticketRequest);
        model.addAttribute("response", response);

        logger.info("Rendering ticket routing result.");

        return "index";
    }
}