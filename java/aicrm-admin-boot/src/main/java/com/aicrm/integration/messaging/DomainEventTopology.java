package com.aicrm.integration.messaging;

public final class DomainEventTopology {
    public static final String EVENT_EXCHANGE = "crm.events";
    public static final String DLQ_EXCHANGE = "crm.events.dlq";
    public static final String DOMAIN_EVENT_QUEUE = "crm.queue.domain-event";
    public static final String DEAD_LETTER_QUEUE = "crm.queue.dead-letter";
    public static final String DOMAIN_EVENT_ROUTING = "crm.#";
    public static final String DEAD_LETTER_ROUTING = "dead.#";

    private DomainEventTopology() {
    }
}
