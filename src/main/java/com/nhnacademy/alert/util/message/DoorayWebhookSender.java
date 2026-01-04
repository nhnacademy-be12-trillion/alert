package com.nhnacademy.alert.util.message;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
public class DoorayWebhookSender {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dooray.webhook.url}")
    private String doorayUrl;

    @Value("${dooray.webhook.analysis.url}")
    private String analysisDoorayUrl;

    public void sendAlert(String messageText) {
        post(doorayUrl, messageText);
    }

    public void sendAnalysis(String messageText) {
        post(analysisDoorayUrl, messageText);
    }

    private void post(String url, String messageText) {
        DoorayPayload payload = new DoorayPayload("⛔️Trillion bot", messageText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<DoorayPayload> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            log.warn("Dooray webhook 전송 실패(url={}): {}", url, e.getMessage());
        }
    }
}
