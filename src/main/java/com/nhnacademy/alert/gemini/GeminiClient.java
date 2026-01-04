package com.nhnacademy.alert.gemini;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@EnableConfigurationProperties(GeminiProperties.class)
public class GeminiClient {

    private final WebClient geminiWebClient;
    private final GeminiProperties prop;

    public GeminiClient(@Qualifier("geminiWebClient") WebClient webClient, GeminiProperties prop) {
        this.geminiWebClient = webClient;
        this.prop = prop;
    }

    public String getGeminiPrompt(String err) {
        String analysisErrorText = err.length() > 797 ? err.substring(0, 797) + "..." : err;
        String prompt = setRequestGeminiPrompt(analysisErrorText);
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            return geminiWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .build(prop.model()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class).flatMap(errorBody -> {
                                log.error("[Gemini API 400 Detail] : {}", errorBody); // 구글의 상세 에러 메시지 출력
                                    return Mono.error(new RuntimeException("잘못된 요청입니다"));
                    }))
                    .bodyToMono(Map.class)
                    .map(response -> {
                        try {
                            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                            Map<String, Object> content = (Map<String, Object>) candidates.getFirst().get("content");
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            return (String) parts.getFirst().get("text");
                        } catch (Exception e) {
                            log.error("응답 파싱 실패: {}", response);
                            return "응답 구조 분석 실패";
                        }
                    })
                    .block(); // 동기 방식으로 결과 반환
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage());
            return "AI 분석 서비스 사용 불가: " + e.getMessage();
        }
    }

    public String setRequestGeminiPrompt(String err) {

        return String.format("""
                *역할: SpringBoot 디버깅 비서
                *규칙: 항목당 100자 이내, 슬랙 스타일(*) 사용
                1.원인: 핵심키워드와 해설
                2.지점: 파일명과 라인(사용자 코드만)
                3.해결: 구체적 조치 및 예시 코드
                에러정보: %s
                """, err);
    }
}
