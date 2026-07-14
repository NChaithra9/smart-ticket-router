package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TicketApiController {

    private final TicketRoutingService ticketRoutingService;

    public TicketApiController(TicketRoutingService ticketRoutingService) {
        this.ticketRoutingService = ticketRoutingService;
    }

    @PostMapping("/route")
    public TicketResponse routeTicket(@RequestBody TicketRequest request) {

        return ticketRoutingService.routeTicket(request.getMessage());

    }

}