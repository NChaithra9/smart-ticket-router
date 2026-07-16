package com.example.smart_ticket_router.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(ExceptionAspect.class);

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