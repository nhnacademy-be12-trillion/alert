package com.nhnacademy.alert.util;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "alert.dedup")
public class AlertDedupProperties {

    private Duration cooldown = Duration.ofMinutes(5);
    private long maxSize = 100_000;
}
