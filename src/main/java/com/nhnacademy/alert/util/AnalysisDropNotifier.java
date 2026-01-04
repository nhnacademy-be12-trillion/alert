package com.nhnacademy.alert.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

@Component
public class AnalysisDropNotifier {

    private final Cache<String, Boolean> onceCache;

    public AnalysisDropNotifier(AlertDedupProperties prop) {
        this.onceCache = Caffeine.newBuilder()
                .expireAfterWrite(prop.getCooldown())
                .maximumSize(prop.getMaxSize())
                .build();
    }

    public boolean shouldNotify(String signature) {
        return onceCache.asMap().putIfAbsent(signature, Boolean.TRUE) == null;
    }
}