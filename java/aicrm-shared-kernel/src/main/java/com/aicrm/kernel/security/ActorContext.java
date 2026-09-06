package com.aicrm.kernel.security;

/** Request-scoped actor storage. It must be cleared by the HTTP interceptor. */
public final class ActorContext {
    private static final ThreadLocal<Actor> CURRENT = new ThreadLocal<>();

    private ActorContext() {
    }

    public static void set(Actor actor) {
        CURRENT.set(actor);
    }

    public static Actor require() {
        Actor actor = CURRENT.get();
        if (actor == null) {
            throw new IllegalStateException("No authenticated actor in context");
        }
        return actor;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
