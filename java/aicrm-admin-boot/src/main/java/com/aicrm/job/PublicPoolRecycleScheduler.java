package com.aicrm.job;

import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.pool.PublicPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Runs deterministic public-pool recycling in small locked batches. */
@Component
@ConditionalOnProperty(prefix = "aicrm.sales.recycle", name = "enabled", havingValue = "true")
public class PublicPoolRecycleScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicPoolRecycleScheduler.class);

    private final SalesRepository repository;
    private final SalesCommandService commandService;
    private final int batchSize;

    public PublicPoolRecycleScheduler(SalesRepository repository, SalesCommandService commandService,
                                      @Value("${aicrm.sales.recycle.batch-size:100}") int batchSize) {
        this.repository = repository;
        this.commandService = commandService;
        this.batchSize = batchSize;
    }

    @Scheduled(cron = "${aicrm.sales.recycle.cron:0 */10 * * * *}", zone = "${aicrm.sales.recycle.zone:UTC}")
    public void recycleExpiredPrivateResources() {
        for (PublicPool pool : repository.findActiveAutoRecyclePools()) {
            try {
                long count = commandService.recycleExpiredPrivateResources(pool, batchSize);
                if (count > 0) {
                    LOGGER.info("Recycled {} {} resources into public pool {} for tenant {}", count,
                            pool.resourceType(), pool.id(), pool.tenantId());
                }
            } catch (RuntimeException exception) {
                LOGGER.error("Public-pool recycle failed for tenant {} pool {}", pool.tenantId(), pool.id(), exception);
            }
        }
    }
}
