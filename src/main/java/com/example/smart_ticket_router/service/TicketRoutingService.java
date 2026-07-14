package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.OpenAIClient;
import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.prompt.PromptBuilder;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketRoutingService {

    private final OpenAIClient openAIClient;
    private final TicketRepository ticketRepository;
    private final EmbeddingService embeddingService;
    private final ObjectMapper mapper = new ObjectMapper();

    public TicketRoutingService(OpenAIClient openAIClient,
                                TicketRepository ticketRepository,
                                EmbeddingService embeddingService) {
        this.openAIClient = openAIClient;
        this.ticketRepository = ticketRepository;
        this.embeddingService = embeddingService;
    }

    public TicketResponse routeTicket(String message) {

        try {

            // Build AI prompt
            String prompt = PromptBuilder.buildPrompt(message);

            // Get AI response
            String json = openAIClient.askOpenAI(prompt);

            // Convert JSON to Java object
            TicketResponse response =
                    mapper.readValue(json, TicketResponse.class);

            // Create ticket entity
            Ticket ticket = new Ticket();

            ticket.setMessage(message);
            ticket.setCategory(response.getCategory());
            ticket.setPriority(response.getPriority());
            ticket.setAssignedTeam(response.getAssignedTeam());
            ticket.setReason(response.getReason());
            ticket.setCreatedAt(LocalDateTime.now());

            // Save ticket in MySQL
            Ticket savedTicket = ticketRepository.save(ticket);

            // Store embedding in ChromaDB
            embeddingService.storeTicket(
                    savedTicket.getId().toString(),
                    savedTicket.getMessage()
            );

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            TicketResponse response = new TicketResponse();
            response.setReason("Unable to process ticket.");

            return response;
        }
    }
}