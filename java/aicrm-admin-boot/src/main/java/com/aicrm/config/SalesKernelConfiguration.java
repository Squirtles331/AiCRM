package com.aicrm.config;

import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.id.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Infrastructure wiring for application-generated business identifiers. */
@Configuration
public class SalesKernelConfiguration {
    @Bean
    public IdGenerator idGenerator(@Value("${aicrm.id.worker-id:0}") long workerId,
                                   @Value("${aicrm.id.datacenter-id:0}") long datacenterId) {
        return new SnowflakeIdGenerator(workerId, datacenterId);
    }
}
