package com.nhnacademy.alert.util;

import com.nhnacademy.alert.AlertEvent;
import com.nhnacademy.alert.gemini.AnalysisDropNotifier;
import com.nhnacademy.alert.util.message.AlertMessageFactory;
import com.nhnacademy.alert.util.message.DoorayWebhookSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisExecutor {

    private final AnalysisDropNotifier dropNotifier;
    private final DoorayWebhookSender dooraySender;
    private final AlertMessageFactory messageFactory;

    private final java.util.concurrent.ThreadPoolExecutor executor =
            new java.util.concurrent.ThreadPoolExecutor(
                    2, 2,
                    30, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(200),
                    r -> new Thread(r, "gemini-analysis-worker"),
                    (r, ex) -> {
                        if (r instanceof AnalysisTask task) {
                            onDropped(task.signature(), task.event());
                        }
                    }
            );

    public void submit(String signature, AlertEvent e, Runnable job) {
        int q = executor.getQueue().size();
        int active = executor.getActiveCount();
        log.info("[ANALYSIS-SUBMIT] sig={}, active={}, q={}", signature, active, q);
        executor.execute(new AnalysisTask(signature, e, job));
    }

    private void onDropped(String signature, AlertEvent e) {
        if (!dropNotifier.shouldNotify(signature)) return;
        dooraySender.sendAnalysis(messageFactory.analysisDrop(e, signature));
    }

    private record AnalysisTask(String signature, AlertEvent event, Runnable delegate) implements Runnable {
        @Override public void run() { delegate.run(); }
    }
}
