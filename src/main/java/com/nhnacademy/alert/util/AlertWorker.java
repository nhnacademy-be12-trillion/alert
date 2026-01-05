package com.nhnacademy.alert.util;


import com.nhnacademy.alert.AlertEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertWorker implements Runnable {

    private final AlertQueue queue;
    private final AlertPipeline pipeline;

    @PostConstruct
    public void start() {
        new Thread(this, "alert-worker").start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                AlertEvent e = queue.take();
                pipeline.handle(e);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                log.warn("AlertWorker loop error", ex);
            }
        }
    }
}

