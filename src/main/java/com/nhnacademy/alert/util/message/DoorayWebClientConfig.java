package com.nhnacademy.alert.util.message;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class DoorayWebClientConfig {

    @Bean
    @Qualifier("doorayWebClient")
    public WebClient doorayWebClient(WebClient.Builder builder) {
        return builder
                .build();
    }
}
