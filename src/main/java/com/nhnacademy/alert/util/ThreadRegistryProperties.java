package com.nhnacademy.alert.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "alert.thread-registry")
public class ThreadRegistryProperties {
    private long ttlMinutes = 30;
    private long maxSize = 100_000;
}
