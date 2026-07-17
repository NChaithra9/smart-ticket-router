package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.OpenAIClient;
import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketCategory;
import com.example.smart_ticket_router.exception.OpenAIException;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TicketRoutingService}.
 *
 * <p>
 * {@link OpenAIClient}, {@link TicketRepository} and
 * {@link EmbeddingService} are all mocked, so these tests exercise the
 * orchestration logic (parsing, persistence, error handling) without
 * making any real network or database calls.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class TicketRoutingServiceTest {

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private TicketRoutingService ticketRoutingService;

    private static final String VALID_JSON =
            "{\"category\":\"BILLING\",\"priority\":\"HIGH\","
            + "\"assignedTeam\":\"FINANCE_SUPPORT\",\"reason\":\"Double charge on invoice\"}";

    @Test
    void routeTicket_savesTicketAndReturnsClassification() {

        when(openAIClient.askOpenAI(anyString())).thenReturn(VALID_JSON);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        TicketResponse response = ticketRoutingService.routeTicket("I was charged twice");

        assertNotNull(response);
        assertEquals("Double charge on invoice", response.getReason());

        verify(ticketRepository).save(any(Ticket.class));
        verify(embeddingService).storeTicket(eq("42"), eq("I was charged twice"));
    }

    @Test
    void routeTicket_withUser_associatesTicketWithUser() {

        when(openAIClient.askOpenAI(anyString())).thenReturn(VALID_JSON);
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = new User();
        user.setEmail("jane@example.com");

        ticketRoutingService.routeTicket("Billing issue", user);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());

        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    void routeTicket_withoutUser_leavesTicketUnassociated() {

        when(openAIClient.askOpenAI(anyString())).thenReturn(VALID_JSON);
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ticketRoutingService.routeTicket("Billing issue");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());

        assertNull(captor.getValue().getUser());
    }

    @Test
    void routeTicket_stripsMarkdownCodeFences() {

        String fenced = "```json\n" + VALID_JSON + "\n```";

        when(openAIClient.askOpenAI(anyString())).thenReturn(fenced);
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response = ticketRoutingService.routeTicket("Billing issue");

        assertNotNull(response);
        assertEquals("Double charge on invoice", response.getReason());
    }

    @Test
    void routeTicket_embeddingFailureDoesNotFailTicketCreation() {

        when(openAIClient.askOpenAI(anyString())).thenReturn(VALID_JSON);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(embeddingService.storeTicket(anyString(), anyString()))
                .thenThrow(new RuntimeException("ChromaDB unavailable"));

        TicketResponse response = assertDoesNotThrow(() ->
                ticketRoutingService.routeTicket("Billing issue"));

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void routeTicket_openAIExceptionPropagatesUnwrapped() {

        when(openAIClient.askOpenAI(anyString()))
                .thenThrow(new OpenAIException("OpenAI is down"));

        assertThrows(OpenAIException.class, () ->
                ticketRoutingService.routeTicket("Billing issue"));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void routeTicket_invalidJsonOnBothAttempts_fallsBackToDefaultClassification() {

        when(openAIClient.askOpenAI(anyString())).thenReturn("not valid json");
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response = ticketRoutingService.routeTicket("Billing issue");

        assertNotNull(response);
        assertEquals(TicketCategory.GENERAL_SUPPORT, response.getCategory());
        assertEquals(Priority.MEDIUM, response.getPriority());

        // Once for the first attempt, once for the retry.
        verify(openAIClient, times(2)).askOpenAI(anyString());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void routeTicket_invalidJsonOnFirstAttemptOnly_succeedsOnRetry() {

        when(openAIClient.askOpenAI(anyString()))
                .thenReturn("not valid json")
                .thenReturn(VALID_JSON);
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response = ticketRoutingService.routeTicket("Billing issue");

        assertNotNull(response);
        assertEquals("Double charge on invoice", response.getReason());

        verify(openAIClient, times(2)).askOpenAI(anyString());
        verify(ticketRepository).save(any(Ticket.class));
    }
}
