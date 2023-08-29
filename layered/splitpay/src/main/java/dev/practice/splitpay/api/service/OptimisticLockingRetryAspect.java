package dev.practice.splitpay.api.service;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OptimisticLockingRetryAspect {

    @Around("@annotation(retryOnOptimisticLocking)")
    public Object retryOnOptimisticLock(ProceedingJoinPoint joinPoint, RetryOnOptimisticLocking retryOnOptimisticLocking) throws Throwable {

        int maxRetry = retryOnOptimisticLocking.value();
        long delay = retryOnOptimisticLocking.delay();

        OptimisticLockingFailureException lockingException = null;

        for(int retryCount = 0; retryCount < maxRetry; retryCount++) {
            try {
                return joinPoint.proceed();
            } catch (OptimisticLockingFailureException ole) {
                lockingException = ole;

                log.info("Optimistic lock attempt {} of {} failed.", retryCount + 1, maxRetry);

                if(retryCount < maxRetry - 1) {
                    try {
                        Thread.sleep(delay); // thread pool 보다 많은 동시 처리가 일어날 경우 thread 부족 현상
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry was interrupted", ie);
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        log.error("Optimistic locking failed after {} attempts.", maxRetry, lockingException);
        throw lockingException;
    }

}
