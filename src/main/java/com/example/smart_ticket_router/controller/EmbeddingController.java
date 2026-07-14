package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.service.EmbeddingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/embedding/test")
    public String testEmbedding() {

        return embeddingService.storeTicket(
                "T101",
                "Unable to login after password reset"
        );
    }
    @GetMapping("/embedding/all")
public String allEmbeddings() {

    return embeddingService.getStoredEmbeddings();
}
}