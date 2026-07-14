package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TicketHistoryController {

    private final TicketRepository ticketRepository;

    public TicketHistoryController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/history")
    public String history(Model model) {

        model.addAttribute(
                "tickets",
                ticketRepository.findAllByOrderByCreatedAtDesc());

        return "history";
    }
}