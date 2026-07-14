package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.ChromaClient;
import com.example.smart_ticket_router.client.OpenAIClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final OpenAIClient openAIClient;
    private final ChromaClient chromaClient;

    // Your Chroma collection ID
    private static final String COLLECTION_ID =
            "401418ee-1fc9-497f-a25d-2e624865a06e";

    public EmbeddingService(OpenAIClient openAIClient,
                            ChromaClient chromaClient) {
        this.openAIClient = openAIClient;
        this.chromaClient = chromaClient;
    }

    // Generate embedding only
    public List<Float> generateEmbedding(String text) {
        return openAIClient.getEmbedding(text);
    }
    public String getStoredEmbeddings() {

    return chromaClient.getDocuments(COLLECTION_ID);
}

    // Generate embedding and store it in ChromaDB
    public String storeTicket(String ticketId, String ticketText) {

        List<Float> embedding = generateEmbedding(ticketText);

        return chromaClient.addEmbedding(
                COLLECTION_ID,
                ticketId,
                ticketText,
                embedding
        );
    }
    public String searchSimilarTickets(String text) {

    List<Float> embedding = generateEmbedding(text);

    return chromaClient.searchSimilar(embedding);
}
}