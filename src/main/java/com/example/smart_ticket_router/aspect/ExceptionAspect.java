package com.example.smart_ticket_router.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cross-cutting aspect that logs every exception thrown from the
 * controller, service, client and repository layers of the
 * application.
 *
 * <p>
 * This centralizes exception logging in one place, guaranteeing that
 * every failure is recorded with its full stack trace before it
 * propagates further — for example to {@code GlobalExceptionHandler},
 * which maps it to an HTTP response, or to the caller of a
 * {@code @Transactional} method, where a rollback is subsequently
 * recorded by {@link TransactionLoggingAspect}.
 * </p>
 *
 * @see LoggingAspect
 * @see TransactionLoggingAspect
 */
@Aspect
@Component
public class ExceptionAspect {

    /**
     * Logger for ExceptionAspect.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(ExceptionAspect.class);

    /**
     * Logs any exception thrown by a method in the {@code controller},
     * {@code service}, {@code client} or {@code repository} packages.
     *
     * <p>
     * The exception is only logged here, not swallowed: it continues
     * to propagate to the caller exactly as if this aspect were not
     * present.
     * </p>
     *
     * @param joinPoint the intercepted method invocation
     * @param exception the exception that was thrown
     */
    @AfterThrowing(
            pointcut =
                    "execution(* com.example.smart_ticket_router.controller..*(..)) || " +
                    "execution(* com.example.smart_ticket_router.service..*(..)) || " +
                    "execution(* com.example.smart_ticket_router.client..*(..)) || " +
                    "execution(* com.example.smart_ticket_router.repository..*(..))",
            throwing = "exception"
    )
    public void logException(
            JoinPoint joinPoint,
            Exception exception) {

        String className =
                joinPoint.getSignature().getDeclaringType().getSimpleName();

        String methodName =
                joinPoint.getSignature().getName();

        logger.error(
                "Exception in {}.{} : {}",
                className,
                methodName,
                exception.getMessage(),
                exception
        );
    }
}