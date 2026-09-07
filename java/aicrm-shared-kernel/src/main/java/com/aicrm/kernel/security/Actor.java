package com.aicrm.kernel.security;

import java.util.Set;

/** Authenticated actor resolved from server-side platform data. */
public record Actor(long tenantId, long userId, long departmentId, String departmentPath,
                    Set<String> roles, Set<String> permissions, Set<DataScope> dataScopes) {
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return hasRole("admin") || permissions.contains(permission);
    }

    public boolean hasDataScope(DataScope scope) {
        return dataScopes.contains(DataScope.ALL) || dataScopes.contains(scope);
    }
}
