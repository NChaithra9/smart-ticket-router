package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.exception.TicketNotFoundException;
import com.example.smart_ticket_router.model.TicketRequest;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.example.smart_ticket_router.service.TicketRoutingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TicketApiController}.
 */
@ExtendWith(MockitoExtension.class)
class TicketApiControllerTest {

    @Mock
    private TicketRoutingService ticketRoutingService;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketApiController ticketApiController;

    @Test
    void routeTicket_delegatesToRoutingService() {

        TicketRequest request = new TicketRequest("My invoice is wrong");

        TicketResponse expected = new TicketResponse();
        expected.setReason("Billing issue");

        when(ticketRoutingService.routeTicket("My invoice is wrong")).thenReturn(expected);

        TicketResponse actual = ticketApiController.routeTicket(request);

        assertSame(expected, actual);
    }

    @Test
    void getTicket_returnsTicketWhenFound() {

        Ticket ticket = new Ticket();
        ticket.setId(7L);

        when(ticketRepository.findById(7L)).thenReturn(Optional.of(ticket));

        Ticket result = ticketApiController.getTicket(7L);

        assertEquals(7L, result.getId());
    }

    @Test
    void getTicket_throwsWhenMissing() {

        when(ticketRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                ticketApiController.getTicket(123L));
    }
}
