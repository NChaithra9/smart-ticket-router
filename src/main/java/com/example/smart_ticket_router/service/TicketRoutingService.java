package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.OpenAIClient;
import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.prompt.PromptBuilder;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketRoutingService {

    private static final Logger logger =
            LoggerFactory.getLogger(TicketRoutingService.class);

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

            logger.info("Received ticket for routing.");

            // Build prompt
            String prompt = PromptBuilder.buildPrompt(message);

            logger.debug("Prompt built successfully.");

            // Ask OpenAI
            logger.info("Sending request to OpenAI...");
            String json = openAIClient.askOpenAI(prompt);

            // Remove markdown if GPT returns ```json ... ```
            json = json.replace("```json", "")
                       .replace("```", "")
                       .trim();

            logger.info("Received response from OpenAI.");
            logger.debug("OpenAI JSON Response: {}", json);

            // Convert JSON to Java object
            TicketResponse response =
                    mapper.readValue(json, TicketResponse.class);

            logger.info("Successfully parsed OpenAI response.");

            // Create Ticket entity
            Ticket ticket = new Ticket();

            ticket.setMessage(message);
            ticket.setCategory(response.getCategory());
            ticket.setPriority(response.getPriority());
            ticket.setAssignedTeam(response.getAssignedTeam());
            ticket.setReason(response.getReason());
            ticket.setCreatedAt(LocalDateTime.now());

            logger.info("Saving ticket to PostgreSQL...");

            Ticket savedTicket = ticketRepository.save(ticket);

            logger.info("Ticket saved successfully. Ticket ID: {}",
                    savedTicket.getId());

            // Store embedding
            try {

                logger.info("Generating embedding for Ticket ID: {}",
                        savedTicket.getId());

                embeddingService.storeTicket(
                        savedTicket.getId().toString(),
                        savedTicket.getMessage()
                );

                logger.info("Embedding stored successfully in ChromaDB.");

            } catch (Exception ex) {

                logger.warn("ChromaDB is unavailable. Embedding storage skipped.");

                logger.debug("ChromaDB Exception:", ex);
            }

            logger.info("Ticket routing completed successfully.");

            return response;

        } catch (Exception e) {

            logger.error("Error while routing ticket.", e);

            throw new RuntimeException("Error while routing ticket", e);
        }
    }
}