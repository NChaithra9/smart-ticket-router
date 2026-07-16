package com.example.smart_ticket_router.config;

import com.example.smart_ticket_router.service.ChromaService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for initializing ChromaDB
 * when the Spring Boot application starts.
 *
 * <p>After all beans are created, this configuration invokes
 * the {@link ChromaService#initialize()} method to ensure that
 * the required ChromaDB collection exists before the application
 * begins processing tickets.
 */
@Configuration
public class ChromaConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(ChromaConfig.class);

    private final ChromaService chromaService;

    /**
     * Constructs a ChromaConfig.
     *
     * @param chromaService service responsible for ChromaDB initialization
     */
    public ChromaConfig(ChromaService chromaService) {
        this.chromaService = chromaService;
    }

    /**
     * Initializes ChromaDB after the Spring application context
     * has been fully created.
     *
     * <p>This method is executed automatically once during
     * application startup.
     *
     * <p>ChromaDB is treated as a best-effort, secondary store
     * throughout the application (see
     * {@code TicketRoutingService#routeTicket}, which already tolerates
     * ChromaDB being unavailable at runtime). For consistency, a
     * failure here is logged as a warning rather than being allowed to
     * abort application startup, so the rest of the application
     * (ticket routing, authentication, admin dashboard, etc.) remains
     * usable even if ChromaDB is temporarily down.
     */
    @PostConstruct
    public void init() {

        logger.info("Initializing ChromaDB.");

        try {
            chromaService.initialize();
            logger.info("ChromaDB initialization completed successfully.");
        } catch (Exception e) {
            logger.warn(
                    "ChromaDB initialization failed. The application will continue "
                    + "to start, but ChromaDB-backed features (embeddings, semantic "
                    + "search) will be degraded until ChromaDB becomes available.",
                    e);
        }
    }
}