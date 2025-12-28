package com.nhnacademy.alert;

import com.nhnacademy.alert.util.AlertQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertQueue alertQueue;

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody AlertEvent event) {
        //큐에 이벤트 주입
        alertQueue.enqueue(event);
        return ResponseEntity.ok().build(); // Fluentd 즉시 OK
    }
}

