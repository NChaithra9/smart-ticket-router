package com.example.smart_ticket_router.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChromaClient {

    @Value("${chroma.url}")
    private String chromaUrl;

    private final RestClient restClient = RestClient.create();

    private static final String TENANT = "default_tenant";
    private static final String DATABASE = "default_database";

    // Check ChromaDB is running
    public String getHeartbeat() {

        return restClient.get()
                .uri(chromaUrl + "/api/v2/heartbeat")
                .retrieve()
                .body(String.class);
    }

    // Create Collection
    public String createCollection(String collectionName) {

        Map<String, Object> request = Map.of(
                "name", collectionName
        );

        return restClient.post()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE + "/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }

    // Get All Collections
    public String getCollections() {

        return restClient.get()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE + "/collections")
                .retrieve()
                .body(String.class);
    }

    // Add Embedding
    public String addEmbedding(String collectionId,
                               String id,
                               String document,
                               List<Float> embedding) {

        Map<String, Object> request = new HashMap<>();

        request.put("ids", List.of(id));
        request.put("documents", List.of(document));
        request.put("embeddings", List.of(embedding));

        return restClient.post()
                .uri(chromaUrl + "/api/v2/tenants/" + TENANT +
                        "/databases/" + DATABASE +
                        "/collections/" + collectionId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
    public String getDocuments(String collectionId) {

    Map<String, Object> request = Map.of(
            "include", List.of("documents", "embeddings")
    );

    return restClient.post()
            .uri(chromaUrl + "/api/v2/tenants/default_tenant/databases/default_database/collections/"
                    + collectionId + "/get")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);
}
public String searchSimilar(List<Float> embedding) {

    Map<String, Object> request = new HashMap<>();

    request.put("query_embeddings", List.of(embedding));
    request.put("n_results", 3);

    return restClient.post()
            .uri(chromaUrl +
                    "/api/v2/tenants/default_tenant/databases/default_database/collections/" +
                    "401418ee-1fc9-497f-a25d-2e624865a06e/query")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);
}
    
}