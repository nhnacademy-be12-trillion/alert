package com.nhnacademy.alert.util;

import com.nhnacademy.alert.AlertEvent;
import com.nhnacademy.alert.util.message.AlertMessageFactory;
import com.nhnacademy.alert.util.message.DoorayWebhookSender;
import com.nhnacademy.alert.util.message.SignatureFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertPipeline {

    private final AlertDeduplicator deduplicator;
    private final SignatureFactory signatureFactory;
    private final AlertMessageFactory messageFactory;

    private final DoorayWebhookSender dooraySender;
    private final AnalysisExecutor analysisExecutor;
    private final AlertAnalysisService analysisService;

    private final ThreadRegistry threadRegistry; // ✅ 추가

    public void handle(AlertEvent e) {
        String signature = signatureFactory.build(e);

        if (!deduplicator.shouldSend(signature)) return;

        // ✅ 이 이벤트 그룹 id 생성/조회
        String groupId = threadRegistry.getOrCreate(signature);

        boolean analyze = analysisService.shouldAnalyze(e);

        // 1) 즉시 알림 (groupId 포함)
        dooraySender.sendAlert(messageFactory.initial(e, signature, groupId, analyze));

        // 2) 분석은 조건부 + 비동기 (groupId도 같이 넘기기)
        if (analyze) {
            analysisExecutor.submit(signature, e,
                    () -> analysisService.analyzeAndSend(e, signature));
        }
    }
}
