package com.nhnacademy.alert.util.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dooray.webhook")
public record DoorayProperties (
        String url,
        String analysis_url,
        String botName
){
}
