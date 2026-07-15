package com.example.smart_ticket_router.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.client.ChromaClient;

/**
 * Service responsible for initializing the ChromaDB collection.
 * <p>
 * This service creates the required collection in ChromaDB
 * during application startup. If the collection already exists,
 * the exception is handled gracefully.
 * </p>
 */
@Service
public class ChromaService {

    /**
     * Logger for ChromaService.
     */
    private static final Logger logger = LoggerFactory.getLogger(ChromaService.class);

    /**
     * Client used to communicate with ChromaDB.
     */
    private final ChromaClient chromaClient;

    /**
     * Constructs a ChromaService with the required Chroma client.
     *
     * @param chromaClient the client used to interact with ChromaDB
     */
    public ChromaService(ChromaClient chromaClient) {
        this.chromaClient = chromaClient;
    }

    /**
     * Initializes the ChromaDB collection.
     * <p>
     * If the collection does not exist, it is created.
     * If it already exists, the exception is caught and
     * logged without interrupting application startup.
     * </p>
     */
    public void initialize() {

        try {

            logger.info("Initializing ChromaDB collection.");

            String response = chromaClient.createCollection("tickets");

            logger.info("ChromaDB collection created successfully.");
            logger.debug("ChromaDB response: {}", response);

        } catch (Exception e) {

            logger.info("ChromaDB collection already exists.");

        }

    }

}