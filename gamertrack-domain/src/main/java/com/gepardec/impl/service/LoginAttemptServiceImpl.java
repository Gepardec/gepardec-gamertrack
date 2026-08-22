package com.gepardec.impl.service;

import com.gepardec.core.services.LoginAttemptService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

@ApplicationScoped
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptServiceImpl.class);

    // Bounds the tracked sources so a distributed attack cannot exhaust memory
    static final int MAX_TRACKED_SOURCES = 10_000;

    @ConfigProperty(name = "login.throttle.max-failures", defaultValue = "5")
    int maxFailures;
    @ConfigProperty(name = "login.throttle.window-seconds", defaultValue = "900")
    long windowSeconds;
    @ConfigProperty(name = "login.throttle.lockout-seconds", defaultValue = "900")
    long lockoutSeconds;

    // Overridable in tests so lockout expiry can be tested without sleeping
    LongSupplier currentTimeMillis = System::currentTimeMillis;

    private final ConcurrentHashMap<String, SourceState> attemptsBySource = new ConcurrentHashMap<>();

    @Override
    public boolean isBlocked(String source) {
        SourceState state = attemptsBySource.get(source);
        return state != null && state.isBlockedAt(currentTimeMillis.getAsLong());
    }

    @Override
    public void loginFailed(String source) {
        long now = currentTimeMillis.getAsLong();
        pruneExpiredIfOverCapacity(now);
        SourceState state = attemptsBySource.computeIfAbsent(source, key -> new SourceState());
        if (state.recordFailure(now, windowSeconds * 1000, maxFailures, lockoutSeconds * 1000)) {
            log.warn("Login source {} blocked for {} seconds after {} failed attempts within {} seconds",
                    source, lockoutSeconds, maxFailures, windowSeconds);
        }
    }

    @Override
    public void loginSucceeded(String source) {
        attemptsBySource.remove(source);
    }

    private void pruneExpiredIfOverCapacity(long now) {
        if (attemptsBySource.size() > MAX_TRACKED_SOURCES) {
            attemptsBySource.entrySet().removeIf(entry -> entry.getValue().isExpiredAt(now, windowSeconds * 1000));
        }
    }

    static final class SourceState {

        private final Deque<Long> failureTimes = new ArrayDeque<>();
        private long blockedUntil;

        synchronized boolean isBlockedAt(long now) {
            return now < blockedUntil;
        }

        synchronized boolean recordFailure(long now, long windowMillis, int maxFailures, long lockoutMillis) {
            failureTimes.addLast(now);
            while (!failureTimes.isEmpty() && failureTimes.peekFirst() <= now - windowMillis) {
                failureTimes.removeFirst();
            }
            if (failureTimes.size() >= maxFailures && now >= blockedUntil) {
                blockedUntil = now + lockoutMillis;
                failureTimes.clear();
                return true;
            }
            return false;
        }

        synchronized boolean isExpiredAt(long now, long windowMillis) {
            return now >= blockedUntil
                    && (failureTimes.isEmpty() || failureTimes.peekLast() <= now - windowMillis);
        }
    }
}
