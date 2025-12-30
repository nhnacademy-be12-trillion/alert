package com.nhnacademy.alert;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlertEvent(
        String service,
        String level,
        String message,
        String traceId,
        String spanId,

        @JsonProperty("@timestamp")
        String timestamp,

        String logger_name,
        String stack_trace_short,

        @JsonIgnore
        Map<String, Object> extra
) {
    @JsonCreator
    public AlertEvent {
        if (extra == null) {
            extra = new HashMap<>();
        }
    }

    @JsonAnySetter
    public void putExtra(String key, Object value) {
        extra.put(key, value);
    }
}
