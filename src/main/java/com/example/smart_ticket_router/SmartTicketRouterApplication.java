package com.example.smart_ticket_router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Smart Ticket Router Spring Boot application.
 * <p>
 * This class bootstraps the Spring Boot application and starts the
 * embedded web server along with all configured Spring components.
 * </p>
 */
@SpringBootApplication
public class SmartTicketRouterApplication {

    /**
     * Logger for SmartTicketRouterApplication.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(SmartTicketRouterApplication.class);

    /**
     * Starts the Smart Ticket Router application.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {

        logger.info("Starting Smart Ticket Router application...");

        SpringApplication.run(SmartTicketRouterApplication.class, args);

        logger.info("Smart Ticket Router application started successfully.");
    }

}