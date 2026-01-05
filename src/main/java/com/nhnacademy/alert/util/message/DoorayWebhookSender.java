package com.nhnacademy.alert.util.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;


@Component
@Slf4j
@EnableConfigurationProperties(DoorayProperties.class)
public class DoorayWebhookSender {

    private final WebClient webClient;

    private final DoorayProperties prop;

    public DoorayWebhookSender(@Qualifier("doorayWebClient") WebClient webClient, DoorayProperties prop) {
        this.webClient = webClient;
        this.prop = prop;
    }

    public void sendAnalysis(String text) {
        post(prop.analysis_url(), text);
    }

    public void sendAlert(String text) {
        post(prop.url(), text);
    }

    private void post(String url, String text) {
        DoorayPayload payload = new DoorayPayload(prop.botName(), text);

        webClient.post()
                .uri(url)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.warn("Dooray 전송 실패: {}", e.toString()))
                .subscribe(
                        success -> {},
                        error -> log.error("Dooray webhook 전송 중 오류 발생", error)
                );
    }
}
