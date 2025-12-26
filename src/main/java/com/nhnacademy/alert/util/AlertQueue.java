package com.nhnacademy.alert.util;


import com.nhnacademy.alert.AlertEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class AlertQueue {

    private final BlockingQueue<AlertEvent> queue =
            new LinkedBlockingQueue<>(1000);

    public void enqueue(AlertEvent event) {
        queue.offer(event);
    }

    public AlertEvent take() throws InterruptedException {
        return queue.take();
    }
}
