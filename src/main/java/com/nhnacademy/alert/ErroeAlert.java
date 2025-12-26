package com.nhnacademy.alert;

public record ErroeAlert(
        String ts,
        String service,
        String level,
        String message,
        String logger,
        String traceId
) {
}
