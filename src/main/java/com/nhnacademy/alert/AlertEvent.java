package com.nhnacademy.alert;

public record AlertEvent(
        String service,
        String level,
        String message,
        String traceId,
        String path,
        String status,
        String timestamp,
        String logger_name,
        String stack_trace_short
) {}
