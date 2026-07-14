package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.client.ChromaClient;
import org.springframework.stereotype.Service;

@Service
public class ChromaService {

    private final ChromaClient chromaClient;

    public ChromaService(ChromaClient chromaClient) {
        this.chromaClient = chromaClient;
    }

    public void initialize() {

        try {

            String response = chromaClient.createCollection("tickets");

            System.out.println("Collection Created:");
            System.out.println(response);

        } catch (Exception e) {

            System.out.println("Collection already exists.");

        }

    }

}