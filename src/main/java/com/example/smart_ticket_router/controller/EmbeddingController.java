package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.service.EmbeddingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for testing embedding-related operations.
 *
 * <p>This controller provides endpoints to:
 * <ul>
 *     <li>Generate and store an embedding for a sample ticket.</li>
 *     <li>Retrieve all stored embeddings from ChromaDB.</li>
 * </ul>
 *
 * <p>These endpoints are intended primarily for development and testing.
 */
@RestController
public class EmbeddingController {

    private static final Logger logger =
            LoggerFactory.getLogger(EmbeddingController.class);

    private final EmbeddingService embeddingService;

    /**
     * Constructs an EmbeddingController.
     *
     * @param embeddingService service responsible for embedding operations
     */
    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    /**
     * Generates and stores an embedding for a sample support ticket.
     *
     * @return response from the embedding storage operation
     */
    @GetMapping("/embedding/test")
    public String testEmbedding() {

        logger.info("Received request to generate and store a sample embedding.");

        String response = embeddingService.storeTicket(
                "T101",
                "Unable to login after password reset"
        );

        logger.info("Sample embedding stored successfully.");

        return response;
    }

    /**
     * Retrieves all stored embeddings from ChromaDB.
     *
     * @return stored embeddings and associated documents
     */
    @GetMapping("/embedding/all")
    public String allEmbeddings() {

        logger.info("Received request to retrieve all stored embeddings.");

        String response = embeddingService.getStoredEmbeddings();

        logger.info("Stored embeddings retrieved successfully.");

        return response;
    }
}