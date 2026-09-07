package com.aicrm.kernel.security;

/** Data visibility scopes granted by roles; multiple roles are combined as a union. */
public enum DataScope {
    SELF,
    DEPARTMENT,
    DEPARTMENT_AND_SUB,
    ALL
}
