package com.nhnacademy.alert.util;


import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertDeduplicator {

    private final Map<String, Instant> cooldownMap = new ConcurrentHashMap<>();
    private static final Duration COOLDOWN = Duration.ofMinutes(5);

    public boolean shouldSend(String key) {
        Instant now = Instant.now();
        Instant last = cooldownMap.get(key);

        if (last == null || last.plus(COOLDOWN).isBefore(now)) {
            cooldownMap.put(key, now);
            return true;
        }
        return false;
    }
}
