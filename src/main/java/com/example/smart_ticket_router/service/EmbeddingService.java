package com.example.smart_ticket_router.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.client.ChromaClient;
import com.example.smart_ticket_router.client.OpenAIClient;
import com.example.smart_ticket_router.exception.EmbeddingException;

/**
 * Service responsible for generating vector embeddings using OpenAI
 * and storing or querying them in ChromaDB.
 * <p>
 * This service provides methods to:
 * <ul>
 *     <li>Generate embeddings for ticket text.</li>
 *     <li>Store ticket embeddings in ChromaDB.</li>
 *     <li>Retrieve stored embeddings.</li>
 *     <li>Search for similar tickets using semantic search.</li>
 * </ul>
 * </p>
 */
@Service
public class EmbeddingService {

    /**
     * Logger for EmbeddingService.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(EmbeddingService.class);

    /**
     * OpenAI client used to generate embeddings.
     */
    private final OpenAIClient openAIClient;

    /**
     * ChromaDB client used for vector storage and retrieval.
     */
    private final ChromaClient chromaClient;

    /**
     * Identifier of the ChromaDB collection.
     */
    private static final String COLLECTION_ID =
            "401418ee-1fc9-497f-a25d-2e624865a06e";

    /**
     * Constructs an EmbeddingService.
     *
     * @param openAIClient client for generating embeddings
     * @param chromaClient client for interacting with ChromaDB
     */
    public EmbeddingService(OpenAIClient openAIClient,
                            ChromaClient chromaClient) {
        this.openAIClient = openAIClient;
        this.chromaClient = chromaClient;
    }

    /**
     * Generates an embedding vector for the given text.
     *
     * @param text the input text
     * @return the embedding vector
     */
    public List<Float> generateEmbedding(String text) {

        logger.info("Generating embedding for input text.");

        return openAIClient.getEmbedding(text);
    }

    /**
     * Retrieves all stored embeddings from the ChromaDB collection.
     *
     * @return the stored embeddings
     * @throws EmbeddingException if ChromaDB cannot be reached or
     *                            returns an error
     */
    public String getStoredEmbeddings() {

        logger.info("Retrieving stored embeddings from ChromaDB.");

        try {

            return chromaClient.getDocuments(COLLECTION_ID);

        } catch (Exception ex) {

            logger.error("Failed to retrieve stored embeddings from ChromaDB.", ex);

            throw new EmbeddingException(
                    "Failed to retrieve stored embeddings from ChromaDB", ex);
        }
    }

    /**
     * Generates an embedding for a support ticket and stores it
     * in the ChromaDB collection.
     *
     * @param ticketId unique identifier of the ticket
     * @param ticketText support ticket content
     * @return response received from ChromaDB
     * @throws EmbeddingException if ChromaDB cannot be reached or
     *                            returns an error while storing the
     *                            embedding
     */
    public String storeTicket(String ticketId, String ticketText) {

        logger.info("Generating and storing embedding for ticket ID: {}", ticketId);

        List<Float> embedding = generateEmbedding(ticketText);

        try {

            String response = chromaClient.addEmbedding(
                    COLLECTION_ID,
                    ticketId,
                    ticketText,
                    embedding
            );

            logger.info("Embedding stored successfully for ticket ID: {}", ticketId);

            return response;

        } catch (Exception ex) {

            logger.error("Failed to store embedding for ticket ID: {}", ticketId, ex);

            throw new EmbeddingException(
                    "Failed to store embedding for ticket ID: " + ticketId, ex);
        }
    }

    /**
     * Searches for tickets similar to the given text using
     * semantic similarity search.
     *
     * @param text the input text
     * @return matching tickets returned by ChromaDB
     * @throws EmbeddingException if ChromaDB cannot be reached or
     *                            returns an error while searching
     */
    public String searchSimilarTickets(String text) {

        logger.info("Searching for similar tickets.");

        List<Float> embedding = generateEmbedding(text);

        try {

            return chromaClient.searchSimilar(embedding);

        } catch (Exception ex) {

            logger.error("Failed to search for similar tickets.", ex);

            throw new EmbeddingException(
                    "Failed to search for similar tickets in ChromaDB", ex);
        }
    }
}