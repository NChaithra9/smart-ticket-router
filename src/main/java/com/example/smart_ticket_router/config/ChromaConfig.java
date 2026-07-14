package com.example.smart_ticket_router.config;

import com.example.smart_ticket_router.service.ChromaService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromaConfig {

    private final ChromaService chromaService;

    public ChromaConfig(ChromaService chromaService) {
        this.chromaService = chromaService;
    }

    @PostConstruct
    public void init() {
        chromaService.initialize();
    }

}