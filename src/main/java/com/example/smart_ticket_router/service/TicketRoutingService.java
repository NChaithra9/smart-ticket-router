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

    // Create ObjectMapper directly
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

            // Build prompt
            String prompt = PromptBuilder.buildPrompt(message);

            // Ask OpenAI
            String json = openAIClient.askOpenAI(prompt);

            // Remove markdown if GPT returns ```json ... ```
            json = json.replace("```json", "")
                       .replace("```", "")
                       .trim();

            System.out.println("========== OPENAI RESPONSE ==========");
            System.out.println(json);
            System.out.println("=====================================");

            // Convert JSON to Java object
            TicketResponse response =
                    mapper.readValue(json, TicketResponse.class);

            // Save ticket
            Ticket ticket = new Ticket();

            ticket.setMessage(message);
            ticket.setCategory(response.getCategory());
            ticket.setPriority(response.getPriority());
            ticket.setAssignedTeam(response.getAssignedTeam());
            ticket.setReason(response.getReason());
            ticket.setCreatedAt(LocalDateTime.now());

            Ticket savedTicket = ticketRepository.save(ticket);

            // Store embedding in ChromaDB
            //embeddingService.storeTicket(
              //      savedTicket.getId().toString(),
                //    savedTicket.getMessage()
            //);
            try {
    embeddingService.storeTicket(
            savedTicket.getId().toString(),
            savedTicket.getMessage()
    );
} catch (Exception e) {
    System.out.println("ChromaDB is not running. Skipping embedding storage.");
}

            return response;

        } catch (Exception e) {

            System.err.println("=========== ERROR ===========");
            e.printStackTrace();
            System.err.println("=============================");

            throw new RuntimeException("Error while routing ticket", e);
        }
    }
}