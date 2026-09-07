package com.aicrm.platform.application;

/** Transport port. Implementations must throw when broker publication is not confirmed. */
@FunctionalInterface
public interface OutboxEventPublisher {
    void publish(OutboxMessage message);
}
