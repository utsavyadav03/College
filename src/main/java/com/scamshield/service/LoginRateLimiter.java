
package com.scamshield.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

@Service
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;

    private static final long BLOCK_DURATION =
            15 * 60 * 1000L;

    private final ConcurrentMap<String, LoginAttempt> attempts =
            new ConcurrentHashMap<>();

    public boolean isBlocked(String ipAddress) {

        LoginAttempt attempt = attempts.get(ipAddress);

        if (attempt == null) {
            return false;
        }

        synchronized (attempt) {

            long currentTime = System.currentTimeMillis();

            if (attempt.blockedUntil > currentTime) {
                return true;
            }

            if (attempt.blockedUntil != 0
                    && attempt.blockedUntil <= currentTime) {

                attempts.remove(ipAddress, attempt);
                return false;
            }

            return false;
        }
    }

    public void recordFailedAttempt(String ipAddress) {

        LoginAttempt attempt = attempts.computeIfAbsent(
                ipAddress, key -> new LoginAttempt());

        synchronized (attempt) {

            long currentTime = System.currentTimeMillis();

            if (attempt.blockedUntil > currentTime) {
                return;
            }

            if (attempt.blockedUntil != 0) {
                attempt.failedAttempts = 0;
                attempt.blockedUntil = 0;
            }

            attempt.failedAttempts++;

            if (attempt.failedAttempts >= MAX_ATTEMPTS) {
                attempt.blockedUntil =
                        currentTime + BLOCK_DURATION;
            }
        }
    }

    public void resetAttempts(String ipAddress) {
        attempts.remove(ipAddress);
    }

    private static class LoginAttempt {

        private int failedAttempts;
        private long blockedUntil;
    }
}
