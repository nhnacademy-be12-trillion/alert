package com.nhnacademy.alert;

public record ErrorAlert(
        String ts,
        String service,
        String level,
        String message,
        String logger,
        String traceId
) {
}
