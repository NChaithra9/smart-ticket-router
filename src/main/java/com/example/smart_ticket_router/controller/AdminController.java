package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.repository.TicketRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsible for handling administrator requests.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>Displaying the administrator dashboard.</li>
 *     <li>Viewing all submitted support tickets.</li>
 * </ul>
 */
@Controller
public class AdminController {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminController.class);

    private final TicketRepository ticketRepository;

    /**
     * Constructs an AdminController.
     *
     * @param ticketRepository repository used to retrieve ticket data
     */
    public AdminController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Displays the administrator dashboard.
     *
     * @return the admin dashboard view
     */
    @GetMapping("/admin")
    public String adminDashboard() {

        logger.info("Admin dashboard requested.");

        return "admin-dashboard";
    }

    /**
     * Displays support tickets.
     * If a priority is selected, only tickets of that priority are shown.
     * Otherwise, all tickets are displayed.
     *
     * @param priority optional priority filter
     * @param model Spring MVC model
     * @return admin tickets view
     */
    @GetMapping("/admin/tickets")
    public String allTickets(
            @RequestParam(required = false) Priority priority,
            Model model) {

        logger.info("Fetching support tickets.");

        var tickets = (priority == null)
                ? ticketRepository.findAllByOrderByCreatedAtDesc()
                : ticketRepository.findByPriorityOrderByCreatedAtDesc(priority);

        logger.debug("Retrieved {} tickets.", tickets.size());

        model.addAttribute("tickets", tickets);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("priorities", Priority.values());

        logger.info("Rendering admin tickets page.");

        return "admin-tickets";
    }
}