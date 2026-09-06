package com.aicrm.kernel.security;

import java.util.Set;

/** Authenticated actor resolved from server-side platform data. */
public record Actor(long tenantId, long userId, Set<String> roles, Set<String> permissions) {
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return hasRole("admin") || permissions.contains(permission);
    }
}
