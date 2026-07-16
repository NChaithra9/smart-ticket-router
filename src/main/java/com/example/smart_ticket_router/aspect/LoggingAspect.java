package com.example.smart_ticket_router.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

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