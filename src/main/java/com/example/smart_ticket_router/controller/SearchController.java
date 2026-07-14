package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.service.EmbeddingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final EmbeddingService embeddingService;

    public SearchController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/tickets/search")
    public String search(@RequestParam String text) {
        return embeddingService.searchSimilarTickets(text);
    }

    @GetMapping("/tickets/all")
    public String allTickets() {
        return embeddingService.getStoredEmbeddings();
    }
}