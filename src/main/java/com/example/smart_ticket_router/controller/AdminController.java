package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final TicketRepository ticketRepository;

    public AdminController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/tickets")
    public String allTickets(Model model) {

        model.addAttribute(
                "tickets",
                ticketRepository.findAllByOrderByCreatedAtDesc()
        );

        return "admin-tickets";
    }
}