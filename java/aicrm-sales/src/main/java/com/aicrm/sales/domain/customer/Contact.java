package com.aicrm.sales.domain.customer;

public record Contact(long id, long tenantId, long customerId, String name, String mobile,
                      String email, String department, String title, boolean decisionMaker, long version) {
}
