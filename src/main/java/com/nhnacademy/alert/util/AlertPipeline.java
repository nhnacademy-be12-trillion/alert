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

        boolean analyze = analysisService.shouldAnalyze(e);

        dooraySender.sendAlert(messageFactory.initial(e, signature, analyze));

        if (analyze) {
            analysisExecutor.submit(signature, e,
                    () -> analysisService.analyzeAndSend(e, signature));
        }
    }
}
