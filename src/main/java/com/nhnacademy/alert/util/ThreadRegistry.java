package com.nhnacademy.alert.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ThreadRegistry {
    private final Cache<String, String> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(30))
                    .maximumSize(100_000)
                    .build();

    public String get(String signature) {
        return cache.getIfPresent(signature);
    }

    public void put(String signature, String threadChannelId) {
        cache.put(signature, threadChannelId);
    }
}
