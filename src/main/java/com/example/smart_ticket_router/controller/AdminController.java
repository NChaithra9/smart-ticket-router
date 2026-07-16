package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketStatus;
import com.example.smart_ticket_router.exception.TicketNotFoundException;
import com.example.smart_ticket_router.repository.TicketRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller responsible for handling administrator requests.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>Displaying the administrator dashboard.</li>
 *     <li>Viewing all submitted support tickets, filterable by
 *     priority and/or workflow status.</li>
 *     <li>Updating the workflow status of a ticket (open, in
 *     progress, resolved, closed).</li>
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
     * @param ticketRepository repository used to retrieve and update
     *                         ticket data
     */
    public AdminController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Displays the administrator dashboard.
     *
     * @return the admin dashboard view
     */
    @PreAuthorize("hasAuthority('VIEW_ALL_TICKETS')")
    @GetMapping("/admin")
    public String adminDashboard() {

        logger.info("Admin dashboard requested.");

        return "admin-dashboard";
    }

    /**
     * Displays support tickets, optionally filtered by priority
     * and/or workflow status.
     *
     * <p>
     * When no priority filter is applied, tickets are sorted by
     * business priority (HIGH first) and then by creation date, so
     * the most urgent open work always surfaces to the top. When a
     * priority is selected, tickets are simply sorted by creation
     * date, since they already share the same priority.
     * </p>
     *
     * @param priority optional priority filter
     * @param status optional workflow status filter
     * @param model Spring MVC model
     * @return admin tickets view
     */
    @PreAuthorize("hasAuthority('VIEW_ALL_TICKETS')")
    @GetMapping("/admin/tickets")
    public String allTickets(
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) TicketStatus status,
            Model model) {

        logger.info("Fetching support tickets. priority={}, status={}",
                priority, status);

        List<Ticket> tickets;

        if (priority != null && status != null) {

            tickets = ticketRepository
                    .findByPriorityAndStatusOrderByCreatedAtDesc(priority, status);

        } else if (priority != null) {

            tickets = ticketRepository
                    .findByPriorityOrderByCreatedAtDesc(priority);

        } else if (status != null) {

            tickets = ticketRepository
                    .findByStatusOrderByPriorityRank(status);

        } else {

            tickets = ticketRepository.findAllOrderByPriorityRank();
        }

        logger.debug("Retrieved {} tickets.", tickets.size());

        model.addAttribute("tickets", tickets);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TicketStatus.values());

        logger.info("Rendering admin tickets page.");

        return "admin-tickets";
    }

    /**
     * Updates the workflow status of a single ticket.
     *
     * <p>
     * Restricted to administrators holding the {@code ASSIGN_TEAM}
     * permission, since managing a ticket's progress through its
     * workflow (open → in progress → resolved → closed) is part of
     * the same ticket-ownership responsibility as assigning it to a
     * team.
     * </p>
     *
     * @param id the ticket to update
     * @param status the new workflow status
     * @return redirect back to the unfiltered admin tickets list
     * @throws TicketNotFoundException if no ticket exists with the
     *                                 given ID
     */
    @PreAuthorize("hasAuthority('ASSIGN_TEAM')")
    @PostMapping("/admin/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {

        logger.info("Updating status of ticket ID {} to {}.", id, status);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Ticket not found. ID: {}", id);

                    return new TicketNotFoundException(
                            "Ticket not found with ID: " + id);
                });

        ticket.setStatus(status);

        ticketRepository.save(ticket);

        logger.info("Ticket ID {} status updated to {}.", id, status);

        return "redirect:/admin/tickets";
    }
}
