package com.example.smart_ticket_router.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Aspect responsible for logging the final outcome — commit or
 * rollback — of every {@code @Transactional} method in the
 * application.
 *
 * <p>
 * This complements {@link LoggingAspect} (method entry/exit and
 * timing) and {@link ExceptionAspect} (exception logging) by adding
 * visibility specifically into the database transaction lifecycle.
 * This is important for diagnosing partial failures: for example, if
 * {@code UserService#registerUser} fails after the user object has
 * been built but before it is fully saved, this aspect logs that the
 * transaction was rolled back and confirms that no partial data was
 * persisted.
 * </p>
 *
 * <p>
 * <b>How it works:</b> the advice wraps every method annotated with
 * {@link org.springframework.transaction.annotation.Transactional}.
 * Because the transaction advisor is configured with a lower
 * (higher-precedence) order than this aspect (see
 * {@code com.example.smart_ticket_router.config.TransactionConfig}),
 * Spring has already started the transaction by the time this advice
 * executes. The advice then registers a
 * {@link TransactionSynchronization} callback that Spring invokes once
 * the surrounding transaction actually completes, logging whether it
 * was committed or rolled back.
 * </p>
 *
 * @see LoggingAspect
 * @see ExceptionAspect
 */
@Aspect
@Component
public class TransactionLoggingAspect {

    /**
     * Logger for TransactionLoggingAspect.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(TransactionLoggingAspect.class);

    /**
     * Wraps every {@code @Transactional} method to report the eventual
     * commit/rollback outcome of its transaction, and to log any
     * exception that causes that transaction to roll back.
     *
     * @param joinPoint the intercepted transactional method invocation
     * @return the value returned by the intercepted method
     * @throws Throwable any exception thrown by the intercepted
     *                    method, rethrown unchanged after being logged
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logTransactionOutcome(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className =
                joinPoint.getSignature().getDeclaringType().getSimpleName();

        String methodName =
                joinPoint.getSignature().getName();

        String operation = className + "." + methodName;

        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCompletion(int status) {

                            if (status == TransactionSynchronization.STATUS_COMMITTED) {

                                logger.info(
                                        "Transaction COMMITTED for {}.",
                                        operation);

                            } else if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {

                                logger.warn(
                                        "Transaction ROLLED BACK for {}. "
                                        + "All changes made during this operation were reverted.",
                                        operation);

                            } else {

                                logger.warn(
                                        "Transaction for {} completed with an unknown status ({}).",
                                        operation,
                                        status);
                            }
                        }
                    }
            );

        } else {

            logger.debug(
                    "No active transaction synchronization found for {}.",
                    operation);
        }

        try {

            return joinPoint.proceed();

        } catch (Throwable ex) {

            logger.error(
                    "Exception raised inside transactional method {}. "
                    + "The transaction will be rolled back. Cause: {}",
                    operation,
                    ex.getMessage());

            throw ex;
        }
    }
}
