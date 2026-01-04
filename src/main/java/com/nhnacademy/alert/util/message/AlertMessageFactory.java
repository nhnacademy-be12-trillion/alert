package com.nhnacademy.alert.util.message;

import com.nhnacademy.alert.AlertEvent;
import org.springframework.stereotype.Component;


@Component
public class AlertMessageFactory {

    public String initial(AlertEvent e, String signature, String groupId, boolean analyze) {
        String tail = analyze
                ? "\n🤖 AI 분석 진행 중… 완료되면 [오류 분석] 채널로 후속 안내합니다."
                : "\nℹ️ (AI 분석 생략)";

        return """
        🚨 오류 발생
        - service: %s
        - level: %s
        - traceId: %s
        - groupId: %s
        - signature: %s
        - logger: %s
        - message: %s
        - time: %s
        %s
        """.formatted(
                n(e.service()),
                n(e.level()),
                n(e.traceId()),
                groupId,
                signature,
                n(e.logger_name()),
                trim(e.message(), 500),
                n(e.timestamp()),
                tail
        );
    }

    public String analysisDrop(AlertEvent e, String signature) {
        return """
            ⚠️ AI 분석 생략됨 (분석 큐 포화)
            - service: %s
            - traceId: %s
            - signature: %s
            - reason: analysisExecutor queue is full (dropped)

            조치:
            - gemini worker 스레드/큐 크기 조정
            - dedup 윈도우 확장 또는 샘플링 적용
            """.formatted(n(e.service()), n(e.traceId()), signature);
    }

    public String analysisFail(AlertEvent e, String signature, Exception ex) {
        return """
            🤖 오류 분석 실패
            - service: %s
            - traceId: %s
            - signature: %s
            - reason: %s

            (참고) stack_trace_short:
            %s
            """.formatted(
                n(e.service()),
                n(e.traceId()),
                signature,
                ex.getClass().getSimpleName(),
                trim(e.stack_trace_short(), 1200)
        );
    }

    private String n(String s) { return s == null ? "" : s; }
    private String trim(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "...(truncated)" : s;
    }
}
