package com.nhnacademy.alert.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class DoorayWebhookSender {
    // WebClient 대신 RestTemplate 사용
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dooray.webhook.url}")
    private String webhookUrl;

    public void send(String messageText) {
        // 1. 페이로드 생성
        DoorayPayload payload = new DoorayPayload("Trillion Alert", messageText);

        try {
            // 2. 요청 헤더 설정 (JSON 타입)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. 데이터와 헤더를 하나로 묶기
            HttpEntity<DoorayPayload> entity = new HttpEntity<>(payload, headers);

            // 4. POST 요청 전송 및 응답 받기
            String response = restTemplate.postForObject(webhookUrl, entity, String.class);

            // 5. 응답 로그 찍기
            log.info("Dooray webhook response body={}", response);

        } catch (Exception e) {
            log.error("Dooray webhook 전송 실패: {}", e.getMessage());
        }
    }
}