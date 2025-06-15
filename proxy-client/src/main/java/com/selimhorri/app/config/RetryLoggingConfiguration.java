package com.selimhorri.app.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.event.RetryOnErrorEvent;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import io.github.resilience4j.retry.event.RetryOnSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RetryLoggingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryLoggingConfiguration.class);

    private final RetryRegistry retryRegistry;

    @Autowired
    public RetryLoggingConfiguration(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerRetryLogging() {
        retryRegistry.getAllRetries().forEach(retry -> {
            retry.getEventPublisher().onRetry(event -> {
                LOGGER.info(
                    "Retrying operation '{}', attempt {}. Last throwable: {}",
                    event.getName(),
                    event.getNumberOfRetryAttempts(),
                    event.getLastThrowable() != null ? event.getLastThrowable().getClass().getName() + ": " + event.getLastThrowable().getMessage() : "N/A"
                );
            });

            retry.getEventPublisher().onError(event -> {
                 LOGGER.warn(
                    "Operation '{}' failed after {} attempts. Last throwable: {}",
                    event.getName(),
                    event.getNumberOfRetryAttempts(),
                    event.getLastThrowable() != null ? event.getLastThrowable().getClass().getName() + ": " + event.getLastThrowable().getMessage() : "N/A"
                );
            });

            retry.getEventPublisher().onSuccess(event -> {
                // Only log if retries actually happened before success
                if (event.getNumberOfRetryAttempts() > 0) {
                    LOGGER.info(
                        "Operation '{}' succeeded after {} attempts. Last throwable during retries: {}",
                        event.getName(),
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable() != null ? event.getLastThrowable().getClass().getName() + ": " + event.getLastThrowable().getMessage() : "N/A"
                    );
                }
            });
        });
    }
}
