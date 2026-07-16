package com.example.smart_ticket_router.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cross-cutting aspect that logs method entry, exit and execution time
 * for every controller, service, client and repository method in the
 * application.
 *
 * <p>
 * This is a textbook use of Spring AOP: instead of adding logging
 * statements to every method by hand, a single {@code @Around} advice
 * is applied declaratively, through a pointcut expression, to an
 * entire slice of the codebase — keeping business logic classes free
 * of repetitive logging boilerplate.
 * </p>
 *
 * @see ExceptionAspect
 * @see TransactionLoggingAspect
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * Logger for LoggingAspect.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Logs the entry, exit and execution time of every method in the
     * {@code controller}, {@code service}, {@code client} and
     * {@code repository} packages.
     *
     * @param joinPoint the intercepted method invocation
     * @return the value returned by the intercepted method
     * @throws Throwable any exception thrown by the intercepted
     *                    method, rethrown unchanged after the
     *                    entry/exit log statements are recorded
     */
    @Around(
            "execution(* com.example.smart_ticket_router.controller..*(..)) || " +
            "execution(* com.example.smart_ticket_router.service..*(..)) || " +
            "execution(* com.example.smart_ticket_router.client..*(..)) || " +
            "execution(* com.example.smart_ticket_router.repository..*(..))"
    )
    public Object logExecutionTime(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className =
                joinPoint.getSignature().getDeclaringType().getSimpleName();

        String methodName =
                joinPoint.getSignature().getName();

        logger.info("Entering {}.{}", className, methodName);

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        logger.info("Exiting {}.{}", className, methodName);

        logger.info(
                "{}.{} executed in {} ms",
                className,
                methodName,
                (end - start)
        );

        return result;
    }
}