package com.aicrm.common.util;

/**
 * 雪花算法 ID 生成器（64 位：1 符号位 + 41 时间戳 + 5 数据中心 + 5 机器 + 12 序列）
 * <p>
 * 单机 4096/毫秒，时钟回拨时等待到下一毫秒。
 * 多实例部署时按实例配置 workerId/datacenterId（0-31）。
 */
public final class SnowflakeIdGenerator {

    /** 默认起点：2025-01-01 00:00:00 UTC */
    private static final long EPOCH = 1735689600000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /** 单机默认实例（workerId=0, datacenterId=0；多实例生产环境请显式 new 并配置） */
    public static final SnowflakeIdGenerator DEFAULT = new SnowflakeIdGenerator(0, 0);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须在 0-" + MAX_WORKER_ID + " 之间");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 必须在 0-" + MAX_DATACENTER_ID + " 之间");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            // 时钟回拨：等待追平，避免 ID 重复
            long offset = lastTimestamp - timestamp;
            if (offset > 5000) {
                throw new IllegalStateException("时钟回拨过大，拒绝生成 ID: " + offset + "ms");
            }
            while (timestamp < lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
