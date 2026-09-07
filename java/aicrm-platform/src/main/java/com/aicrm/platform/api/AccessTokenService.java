package com.aicrm.platform.api;

import com.aicrm.kernel.security.Actor;

/** Token port implemented by the runtime infrastructure. */
public interface AccessTokenService {
    String issue(Actor actor);

    TokenIdentity parse(String token);

    long expiresInSeconds();

    record TokenIdentity(long tenantId, long userId) {
    }
}
