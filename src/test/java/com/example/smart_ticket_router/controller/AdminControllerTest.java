package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketStatus;
import com.example.smart_ticket_router.exception.TicketNotFoundException;
import com.example.smart_ticket_router.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AdminController}.
 *
 * <p>
 * Verifies that the correct {@link TicketRepository} query is chosen
 * for each combination of priority/status filter, and that the
 * status-update endpoint correctly loads, mutates and saves a ticket
 * (or throws {@link TicketNotFoundException} when it does not exist).
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private AdminController adminController;

    @Test
    void allTickets_noFilters_ordersByPriorityRank() {

        when(ticketRepository.findAllOrderByPriorityRank()).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        String view = adminController.allTickets(null, null, model);

        assertEquals("admin-tickets", view);
        verify(ticketRepository).findAllOrderByPriorityRank();
        verify(ticketRepository, never()).findByPriorityOrderByCreatedAtDesc(any());
        verify(ticketRepository, never()).findByStatusOrderByPriorityRank(any());
    }

    @Test
    void allTickets_priorityOnly_filtersByPriority() {

        when(ticketRepository.findByPriorityOrderByCreatedAtDesc(Priority.HIGH))
                .thenReturn(List.of());

        Model model = new ExtendedModelMap();
        adminController.allTickets(Priority.HIGH, null, model);

        verify(ticketRepository).findByPriorityOrderByCreatedAtDesc(Priority.HIGH);
        verify(ticketRepository, never()).findAllOrderByPriorityRank();
    }

    @Test
    void allTickets_statusOnly_filtersByStatus() {

        when(ticketRepository.findByStatusOrderByPriorityRank(TicketStatus.OPEN))
                .thenReturn(List.of());

        Model model = new ExtendedModelMap();
        adminController.allTickets(null, TicketStatus.OPEN, model);

        verify(ticketRepository).findByStatusOrderByPriorityRank(TicketStatus.OPEN);
        verify(ticketRepository, never()).findAllOrderByPriorityRank();
    }

    @Test
    void allTickets_bothFilters_combinesPriorityAndStatus() {

        when(ticketRepository.findByPriorityAndStatusOrderByCreatedAtDesc(
                Priority.LOW, TicketStatus.RESOLVED)).thenReturn(List.of());

        Model model = new ExtendedModelMap();
        adminController.allTickets(Priority.LOW, TicketStatus.RESOLVED, model);

        verify(ticketRepository).findByPriorityAndStatusOrderByCreatedAtDesc(
                Priority.LOW, TicketStatus.RESOLVED);
    }

    @Test
    void updateTicketStatus_updatesAndSavesTicket() {

        Ticket ticket = new Ticket();
        ticket.setId(5L);
        ticket.setStatus(TicketStatus.OPEN);

        when(ticketRepository.findById(5L)).thenReturn(Optional.of(ticket));

        String view = adminController.updateTicketStatus(5L, TicketStatus.RESOLVED);

        assertEquals("redirect:/admin/tickets", view);
        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicketStatus_ticketMissing_throwsTicketNotFoundException() {

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                adminController.updateTicketStatus(99L, TicketStatus.RESOLVED));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
