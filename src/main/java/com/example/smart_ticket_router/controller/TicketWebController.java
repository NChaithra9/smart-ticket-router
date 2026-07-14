package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.service.TicketRoutingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TicketWebController {

    private final TicketRoutingService ticketRoutingService;

    public TicketWebController(TicketRoutingService ticketRoutingService) {
        this.ticketRoutingService = ticketRoutingService;
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

        model.addAttribute("ticketRequest", ticketRequest);
        model.addAttribute("response", response);

        return "index";
    }

}