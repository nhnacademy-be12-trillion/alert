package com.nhnacademy.alert.gemini;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AnalysisDropNotifier {
    // signature 기준으로 5분에 1회만 "분석 생략" 알림
    private final Cache<String, Boolean> onceCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(100_000)
            .build();

    public boolean shouldNotify(String signature) {
        // 없으면 true(알림 전송), 있으면 false(이미 보냄)
        return onceCache.asMap().putIfAbsent(signature, Boolean.TRUE) == null;
    }
}
