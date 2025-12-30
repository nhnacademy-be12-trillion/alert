package com.nhnacademy.alert.util;


import com.nhnacademy.alert.AlertEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertWorker {

    private final AlertQueue queue;
    private final AlertDeduplicator deduplicator;
    private final DoorayWebhookSender sender;

    @PostConstruct
    public void start() {
        new Thread(this::loop, "alert-worker").start();
    }

    private void loop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                AlertEvent e = queue.take();
                log.info("****** service signature part: {}", e.service());
                String service = normalizeService(e.service());

                String signature = String.join("|",
                        service,
                        e.level(),
                        e.logger_name()
                );
                boolean shouldSend = deduplicator.shouldSend(signature);
                log.info("****** signature + shouldSend bool type: {} + {}", signature, shouldSend);
                if (!shouldSend) {
                continue;
            }

            String text = """
                    🚨 ERROR 발생
                    서비스: %s
                    레벨: %s
                    트레이스 ID: %s
                    로거네임: %s
                    메시지: %s
                    시간: %s
                    stack_trace(일부):%s
                    """
                    .formatted(
                            service,
                            e.level(),
                            e.traceId(),
                            e.logger_name(),
                            e.message(),
                            e.timestamp(),
                            e.stack_trace_short()
                    );
                sender.send(text);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                log.warn("Dooray webhook failed", ex);
            }
        }
    }

    private String normalizeService(String service) {
        if (service == null || service.isBlank()) return "unknown";
        return service.replaceAll("(-peer-?\\d+)$", "");
    }

}

