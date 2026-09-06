package com.aicrm.kernel.id;

import java.time.Instant;

/** Monotonic 64-bit identifier generator. Worker and datacenter IDs are configuration owned. */
public final class SnowflakeIdGenerator implements IdGenerator {
    private static final long EPOCH = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();
    private static final long SEQUENCE_MASK = (1L << 12) - 1;
    private final long workerId;
    private final long datacenterId;
    private long sequence;
    private long lastTimestamp = -1;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > 31 || datacenterId < 0 || datacenterId > 31) {
            throw new IllegalArgumentException("workerId and datacenterId must be between 0 and 31");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    @Override
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards; refusing to generate an ID");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                do {
                    timestamp = System.currentTimeMillis();
                } while (timestamp <= lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << 22) | (datacenterId << 17) | (workerId << 12) | sequence;
    }
}
