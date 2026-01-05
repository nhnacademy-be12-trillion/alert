package com.nhnacademy.alert.gemini;

import org.springframework.stereotype.Component;

@Component
public class GeminiTextFormatter {

    public String analyzeFormat(String service, String traceId, String signature, String aiText) {

        return """
            🤖 오류 분석 결과
            - service: %s
            - traceId: %s
            - signature: %s

            %s
            """.formatted(
                n(service),
                n(traceId),
                n(signature),
                safeTrim(aiText, 4000)
        );
    }

    private String n(String s) {
        return s == null ? "-" : s;
    }

    private String safeTrim(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "\n...(truncated)";
    }
}
