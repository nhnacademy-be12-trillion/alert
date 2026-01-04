package com.nhnacademy.alert.gemini;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
record GeminiProperties(
        String apiKey,
        String model
) {
}
