package com.nhnacademy.alert.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@EnableConfigurationProperties(AlertDedupProperties.class)
public class AlertDeduplicator {

    private final Duration cooldown;
    private final Cache<String, Instant> cache;

    public AlertDeduplicator(AlertDedupProperties props) {
        this.cooldown = props.getCooldown();
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(cooldown)
                .maximumSize(props.getMaxSize())
                .build();
    }

    public boolean shouldSend(String key) {
        Instant now = Instant.now();
        final boolean[] send = {false};

        cache.asMap().compute(key, (k, last) -> {
            if (last == null || last.plus(cooldown).isBefore(now)) {
                send[0] = true;
                return now;
            }
            return last;
        });
        return send[0];
    }
}