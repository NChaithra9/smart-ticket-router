package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TicketHistoryController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketHistoryController(
            TicketRepository ticketRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-tickets")
    public String myTickets(Model model) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        model.addAttribute(
                "tickets",
                ticketRepository.findByUser(user)
        );

        return "history";
    }
}