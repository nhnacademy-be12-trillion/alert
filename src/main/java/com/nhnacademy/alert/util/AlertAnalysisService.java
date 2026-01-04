package com.nhnacademy.alert.util;

import com.nhnacademy.alert.AlertEvent;
import com.nhnacademy.alert.gemini.GeminiClient;
import com.nhnacademy.alert.gemini.GeminiTextFormatter;
import com.nhnacademy.alert.util.message.AlertMessageFactory;
import com.nhnacademy.alert.util.message.DoorayWebhookSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertAnalysisService {

    private final GeminiClient geminiClient;
    private final GeminiTextFormatter textFormatter;
    private final DoorayWebhookSender dooraySender;
    private final AlertMessageFactory messageFactory;

    public boolean shouldAnalyze(AlertEvent e) {
        boolean isError = e.level() != null && "ERROR".equalsIgnoreCase(e.level().trim());
        boolean hasStack = e.stack_trace_short() != null && !e.stack_trace_short().trim().isEmpty();
        return isError && hasStack;
    }

    public void analyzeAndSend(AlertEvent e, String signature) {
        try {
            String aiText = geminiClient.getGeminiPrompt(e.stack_trace_short());
            String pretty = textFormatter.analyzeFormat(n(e.service()), n(e.traceId()), signature, aiText);
            dooraySender.sendAnalysis(pretty);
        } catch (Exception ex) {
            dooraySender.sendAnalysis(messageFactory.analysisFail(e, signature, ex));
        }
    }


    private String n(String s) { return s == null ? "" : s; }
}
