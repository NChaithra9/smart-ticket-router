package com.example.smart_ticket_router.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client responsible for interacting with ChromaDB using its REST API.
 *
 * <p>This client provides methods to:
 * <ul>
 *     <li>Check ChromaDB health.</li>
 *     <li>Create collections.</li>
 *     <li>Retrieve collections.</li>
 *     <li>Store document embeddings.</li>
 *     <li>Retrieve stored documents.</li>
 *     <li>Perform similarity search using embeddings.</li>
 * </ul>
 */
@Component
public class ChromaClient {

    private static final Logger logger =
            LoggerFactory.getLogger(ChromaClient.class);

    @Value("${chroma.url}")
    private String chromaUrl;

    private final RestClient restClient = RestClient.create();

    private static final String TENANT = "default_tenant";
    private static final String DATABASE = "default_database";

    /**
     * Checks whether the ChromaDB server is running.
     *
     * @return heartbeat response from ChromaDB
     */
    public String getHeartbeat() {

        logger.info("Checking ChromaDB heartbeat.");

        String response = restClient.get()
                .uri(chromaUrl + "/api/v2/heartbeat")
                .retrieve()
                .body(String.class);

        logger.debug("Heartbeat response: {}", response);

        return response;
    }

    /**
     * Creates a new collection in ChromaDB.
     *
     * @param collectionName name of the collection
     * @return API response from ChromaDB
     */
    public String createCollection(String collectionName) {

        logger.info("Creating ChromaDB collection: {}", collectionName);

        Map<String, Object> request = Map.of(
                "name", collectionName
        );

        String response = restClient.post()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE + "/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        logger.debug("Create collection response: {}", response);

        return response;
    }

    /**
     * Retrieves all collections from ChromaDB.
     *
     * @return JSON response containing all collections
     */
    public String getCollections() {

        logger.info("Fetching all ChromaDB collections.");

        String response = restClient.get()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE + "/collections")
                .retrieve()
                .body(String.class);

        logger.debug("Collections response: {}", response);

        return response;
    }

    /**
     * Stores a document and its embedding into a ChromaDB collection.
     *
     * @param collectionId collection identifier
     * @param id unique document ID
     * @param document document text
     * @param embedding embedding vector
     * @return API response
     */
    public String addEmbedding(String collectionId,
                               String id,
                               String document,
                               List<Float> embedding) {

        logger.info("Adding embedding for document ID: {}", id);

        Map<String, Object> request = new HashMap<>();
        request.put("ids", List.of(id));
        request.put("documents", List.of(document));
        request.put("embeddings", List.of(embedding));

        String response = restClient.post()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE +
                        "/collections/" + collectionId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        logger.debug("Add embedding response: {}", response);

        return response;
    }

    /**
     * Retrieves all stored documents and embeddings from a collection.
     *
     * @param collectionId collection identifier
     * @return JSON response containing documents and embeddings
     */
    public String getDocuments(String collectionId) {

        logger.info("Fetching documents from collection: {}", collectionId);

        Map<String, Object> request = Map.of(
                "include", List.of("documents", "embeddings")
        );

        String response = restClient.post()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE +
                        "/collections/" + collectionId + "/get")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        logger.debug("Documents response: {}", response);

        return response;
    }

    /**
     * Searches for the most similar documents using the supplied embedding.
     *
     * <p>Returns the top three matching documents.
     *
     * @param embedding query embedding vector
     * @return similarity search results
     */
    public String searchSimilar(List<Float> embedding) {

        logger.info("Performing similarity search in ChromaDB.");

        Map<String, Object> request = new HashMap<>();
        request.put("query_embeddings", List.of(embedding));
        request.put("n_results", 3);

        String response = restClient.post()
                .uri(chromaUrl +
                        "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE +
                        "/collections/401418ee-1fc9-497f-a25d-2e624865a06e/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        logger.debug("Similarity search response: {}", response);

        return response;
    }
}