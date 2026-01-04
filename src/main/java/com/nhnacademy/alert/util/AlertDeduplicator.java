package com.nhnacademy.alert.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@EnableConfigurationProperties(AlertDedupProperties.class)
public class AlertDeduplicator {

    private final Cache<String, Instant> cache;

    public AlertDeduplicator(AlertDedupProperties props) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(props.getCooldown())
                .maximumSize(props.getMaxSize())
                .build();
    }

    public boolean shouldSend(String key) {
        Instant lastSent = cache.getIfPresent(key);

        if (lastSent == null) {
            cache.put(key, Instant.now());
            return true;
        }
        return false;
    }
}