package com.example.smart_ticket_router.controller;
import com.example.smart_ticket_router.service.EmbeddingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for semantic search operations.
 *
 * <p>This controller provides endpoints to:
 * <ul>
 *     <li>Search for support tickets using semantic similarity.</li>
 *     <li>Retrieve all stored ticket embeddings.</li>
 * </ul>
 *
 * <p>Semantic search is performed by generating an embedding for the
 * query text and comparing it against embeddings stored in ChromaDB.
 */
@RestController
public class SearchController {

    private static final Logger logger =
            LoggerFactory.getLogger(SearchController.class);

    private final EmbeddingService embeddingService;

    /**
     * Constructs a SearchController.
     *
     * @param embeddingService service responsible for embedding generation
     *                         and semantic search
     */
    public SearchController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    /**
     * Searches for tickets that are semantically similar to the
     * supplied query text.
     *
     * @param text search query entered by the user
     * @return search results from ChromaDB
     */
    @GetMapping("/tickets/search")
    public String search(@RequestParam String text) {

        logger.info("Received semantic search request.");

        String response = embeddingService.searchSimilarTickets(text);

        logger.info("Semantic search completed successfully.");

        return response;
    }

    /**
     * Retrieves all stored ticket embeddings.
     *
     * @return stored ticket embeddings and associated documents
     */
    @GetMapping("/tickets/all")
    public String allTickets() {

        logger.info("Received request to retrieve all stored ticket embeddings.");

        String response = embeddingService.getStoredEmbeddings();

        logger.info("Stored ticket embeddings retrieved successfully.");

        return response;
    }
}