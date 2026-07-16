package com.example.smart_ticket_router.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Enables Spring's declarative transaction management for the
 * application.
 *
 * <p>
 * The transaction advisor created by
 * {@link EnableTransactionManagement} is explicitly given a
 * high-precedence order ({@code order = 0}) so that, in the chain of
 * AOP proxies wrapping a {@code @Transactional} method, it sits
 * <b>outside</b>
 * {@code com.example.smart_ticket_router.aspect.TransactionLoggingAspect}.
 * </p>
 *
 * <p>
 * In practice this guarantees that by the time
 * {@code TransactionLoggingAspect} runs, Spring has already started the
 * database transaction for the method being called. This allows that
 * aspect to reliably register a callback with
 * {@code TransactionSynchronizationManager} and be notified whether the
 * transaction was ultimately committed or rolled back.
 * </p>
 */
@Configuration
@EnableTransactionManagement(order = 0)
public class TransactionConfig {
}
