package com.nhnacademy.alert.gemini;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiClient {

    private final WebClient geminiWebClient;
    private final GeminiProperties prop;

    public GeminiClient(@Qualifier("geminiWebClient") WebClient webClient, GeminiProperties prop) {
        this.geminiWebClient = webClient;
        this.prop = prop;
    }

    public String getGeminiPrompt(String err) {
        String analysisErrorText = safeTrim(err, 800);
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
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class).flatMap(errorBody -> {
                                log.error("[Gemini API Error] status={}, body={}",
                                        response.statusCode(), errorBody);
                                return Mono.error(new RuntimeException("Gemini API error: " + response.statusCode()));
                            })
                    )
                    .bodyToMono(Map.class)
                    .map(this::extractText)
                    .block(java.time.Duration.ofSeconds(15));
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류", e);
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

    private String safeTrim(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "Gemini 응답에 candidates가 없습니다";

            Map<String, Object> content = (Map<String, Object>) candidates.getFirst().get("content");
            if (content == null) return "Gemini 응답에 content가 없습니다";

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) return "Gemini 응답에 parts가 없습니다";

            return String.valueOf(parts.getFirst().get("text"));
        } catch (Exception e) {
            log.error("응답 파싱 실패: {}", response, e);
            return "응답 구조 분석 실패";
        }
    }
}
