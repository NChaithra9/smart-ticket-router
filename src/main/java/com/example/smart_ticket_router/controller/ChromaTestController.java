package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.client.ChromaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChromaTestController {

    private final ChromaClient chromaClient;

    public ChromaTestController(ChromaClient chromaClient) {
        this.chromaClient = chromaClient;
    }

    @GetMapping("/chroma/test")
    public String test() {
        return chromaClient.getHeartbeat();
    }

    @GetMapping("/chroma/create")
    public String createCollection() {
        return chromaClient.createCollection("tickets");
    }

    @GetMapping("/chroma/collections")
    public String collections() {
        return chromaClient.getCollections();
    }
}