package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.OpenAIClient;
import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.exception.OpenAIException;
import com.example.smart_ticket_router.model.TicketResponse;
import com.example.smart_ticket_router.prompt.PromptBuilder;
import com.example.smart_ticket_router.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for classifying incoming support tickets with
 * OpenAI, persisting the result, and storing a semantic embedding of
 * the ticket in ChromaDB.
 *
 * <p>
 * This is the central orchestration point of the application: it wires
 * together the {@link OpenAIClient} (AI classification), the
 * {@link TicketRepository} (relational persistence) and the
 * {@link EmbeddingService} (vector storage used for semantic search).
 * </p>
 *
 * <p>
 * <b>Transactional behaviour:</b> the relational write performed here
 * (saving the {@link Ticket}) is wrapped in a Spring-managed transaction
 * via {@link Transactional}. If an unexpected error occurs while
 * building or persisting the ticket, the transaction is rolled back and
 * that rollback is recorded by
 * {@code com.example.smart_ticket_router.aspect.TransactionLoggingAspect},
 * an AOP component that observes the completion of every
 * {@code @Transactional} method in the application. Storing the
 * embedding in ChromaDB is intentionally kept outside of that rollback
 * path: ChromaDB is treated as a best-effort, secondary store, so if it
 * is temporarily unavailable the ticket itself is still saved
 * successfully and only a warning is logged.
 * </p>
 */
@Service
public class TicketRoutingService {

    /**
     * Logger for TicketRoutingService.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TicketRoutingService.class);

    /**
     * Client used to classify tickets with OpenAI.
     */
    private final OpenAIClient openAIClient;

    /**
     * Repository used to persist tickets.
     */
    private final TicketRepository ticketRepository;

    /**
     * Service used to store/query ticket embeddings in ChromaDB.
     */
    private final EmbeddingService embeddingService;

    /**
     * JSON mapper used to parse the classification response from OpenAI.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs a TicketRoutingService.
     *
     * @param openAIClient      client used to classify tickets with OpenAI
     * @param ticketRepository  repository used to persist tickets
     * @param embeddingService  service used to store/query embeddings
     */
    public TicketRoutingService(OpenAIClient openAIClient,
                                TicketRepository ticketRepository,
                                EmbeddingService embeddingService) {

        this.openAIClient = openAIClient;
        this.ticketRepository = ticketRepository;
        this.embeddingService = embeddingService;
    }

    /**
     * Routes an anonymous/system support ticket.
     *
     * <p>
     * Used by the public REST API ({@code POST /api/route}), where the
     * ticket is not necessarily linked to an authenticated user. Delegates
     * to {@link #routeTicket(String, User)} with a {@code null} user.
     * </p>
     *
     * @param message the raw ticket message submitted by the caller
     * @return the AI-generated classification of the ticket
     */
    @Transactional
    public TicketResponse routeTicket(String message) {

        // Self-invocation note: this call does not go through the Spring
        // proxy, so it does not open a *second* transaction. It simply
        // executes within the transaction already started for this
        // method by the @Transactional annotation above, which is exactly
        // the behaviour we want here.
        return routeTicket(message, null);
    }

    /**
     * Routes a support ticket and associates it with the given user.
     *
     * <p>
     * Used by the web UI ({@code POST /route}), so that the ticket is
     * classified, persisted and linked to the submitting user as a
     * single atomic operation. This avoids the previous bug where the
     * web controller saved a second, duplicate ticket row on top of the
     * one already saved here.
     * </p>
     *
     * @param message the raw ticket message submitted by the user
     * @param user    the authenticated user submitting the ticket,
     *                or {@code null} for anonymous/system submissions
     * @return the AI-generated classification of the ticket
     * @throws OpenAIException if OpenAI cannot be reached or its
     *                         response cannot be parsed
     * @throws RuntimeException if any other unexpected error occurs
     *                          while routing the ticket
     */
    @Transactional
    public TicketResponse routeTicket(String message, User user) {

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

            if (user != null) {

                ticket.setUser(user);

                logger.debug("Associating ticket with user: {}", user.getEmail());
            }

            logger.info("Saving ticket to the database...");

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

                // ChromaDB is a best-effort secondary store. Its failure
                // must not roll back the ticket that was already saved
                // successfully above.
                logger.warn("ChromaDB is unavailable. Embedding storage skipped.");

                logger.debug("ChromaDB Exception:", ex);
            }

            logger.info("Ticket routing completed successfully.");

            return response;

        } catch (OpenAIException ex) {

            // Preserve the specific exception type so GlobalExceptionHandler
            // can return an accurate HTTP status/message for OpenAI failures,
            // instead of it being swallowed into a generic RuntimeException.
            logger.error("OpenAI error while routing ticket.", ex);

            throw ex;

        } catch (Exception e) {

            logger.error("Error while routing ticket.", e);

            throw new RuntimeException("Error while routing ticket", e);
        }
    }
}
