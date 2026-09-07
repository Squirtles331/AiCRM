package com.aicrm.sales.application;

/** Result of an atomic employee-exit handover batch. */
public record BatchHandoverResult(String batchNo, long leadCount, long customerCount, long remainingCount) {
}
