package com.aicrm.integration.messaging;

import com.aicrm.platform.application.OutboxDispatchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Duration;

/** Periodically drains committed outbox records; disabled until explicitly configured. */
@Component
@ConditionalOnProperty(prefix = "aicrm.outbox", name = "enabled", havingValue = "true")
public class OutboxDispatchScheduler {
    private final OutboxDispatchService dispatchService;
    private final int batchSize;
    private final Duration leaseDuration;
    private final String workerId = ManagementFactory.getRuntimeMXBean().getName();

    public OutboxDispatchScheduler(OutboxDispatchService dispatchService,
                                   @Value("${aicrm.outbox.batch-size:100}") int batchSize,
                                   @Value("${aicrm.outbox.lease-seconds:300}") long leaseSeconds) {
        this.dispatchService = dispatchService;
        this.batchSize = batchSize;
        this.leaseDuration = Duration.ofSeconds(leaseSeconds);
    }

    @Scheduled(fixedDelayString = "${aicrm.outbox.interval-millis:5000}")
    public void dispatch() {
        dispatchService.dispatchBatch(workerId, batchSize, leaseDuration);
    }
}
