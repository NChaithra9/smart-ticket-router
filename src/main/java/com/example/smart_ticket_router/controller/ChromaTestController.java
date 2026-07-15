package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.client.ChromaClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller used for testing ChromaDB connectivity and operations.
 *
 * <p>This controller exposes endpoints to:
 * <ul>
 *     <li>Verify that ChromaDB is running.</li>
 *     <li>Create a collection.</li>
 *     <li>Retrieve all collections.</li>
 * </ul>
 *
 * <p>These endpoints are intended for development and testing purposes.
 */
@RestController
public class ChromaTestController {

    private static final Logger logger =
            LoggerFactory.getLogger(ChromaTestController.class);

    private final ChromaClient chromaClient;

    /**
     * Constructs a ChromaTestController.
     *
     * @param chromaClient client used to communicate with ChromaDB
     */
    public ChromaTestController(ChromaClient chromaClient) {
        this.chromaClient = chromaClient;
    }

    /**
     * Checks whether the ChromaDB server is running.
     *
     * @return heartbeat response from ChromaDB
     */
    @GetMapping("/chroma/test")
    public String test() {

        logger.info("Received request to test ChromaDB heartbeat.");

        String response = chromaClient.getHeartbeat();

        logger.info("ChromaDB heartbeat retrieved successfully.");

        return response;
    }

    /**
     * Creates the default "tickets" collection in ChromaDB.
     *
     * @return API response after collection creation
     */
    @GetMapping("/chroma/create")
    public String createCollection() {

        logger.info("Received request to create ChromaDB collection: tickets.");

        String response = chromaClient.createCollection("tickets");

        logger.info("Collection creation request completed.");

        return response;
    }

    /**
     * Retrieves all collections from ChromaDB.
     *
     * @return JSON response containing all collections
     */
    @GetMapping("/chroma/collections")
    public String collections() {

        logger.info("Received request to fetch ChromaDB collections.");

        String response = chromaClient.getCollections();

        logger.info("Collections retrieved successfully.");

        return response;
    }
}