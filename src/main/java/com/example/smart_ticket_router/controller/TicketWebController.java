package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.repository.UserRepository;
import com.example.smart_ticket_router.service.TicketRoutingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TicketWebController {

    private final TicketRoutingService ticketRoutingService;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public TicketWebController(
            TicketRoutingService ticketRoutingService,
            UserRepository userRepository,
            TicketRepository ticketRepository) {

        this.ticketRoutingService = ticketRoutingService;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("ticketRequest", new TicketRequest());

        return "index";
    }

    @PostMapping("/route")
    public String routeTicket(
            @ModelAttribute TicketRequest ticketRequest,
            Model model) {

        TicketResponse response =
                ticketRoutingService.routeTicket(ticketRequest.getMessage());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Ticket ticket = new Ticket();

        ticket.setMessage(ticketRequest.getMessage());
        ticket.setCategory(response.getCategory());
        ticket.setPriority(response.getPriority());
        ticket.setAssignedTeam(response.getAssignedTeam());
        ticket.setReason(response.getReason());
        ticket.setUser(user);

        ticketRepository.save(ticket);

        model.addAttribute("ticketRequest", ticketRequest);
        model.addAttribute("response", response);

        return "index";
    }
}