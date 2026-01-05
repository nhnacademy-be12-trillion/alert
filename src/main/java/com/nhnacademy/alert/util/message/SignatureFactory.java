package com.nhnacademy.alert.util.message;


import com.nhnacademy.alert.AlertEvent;
import org.springframework.stereotype.Component;

@Component
public class SignatureFactory {

    public String build(AlertEvent e) {
        String service = normalizeService(e.service());
        return String.join("|",
                n(service),
                n(e.level()),
                n(e.logger_name())
        );
    }

    private String normalizeService(String service) {
        if (service == null || service.isBlank()) return "unknown";
        return service.replaceAll("(-peer-?\\d+)$", "");
    }

    private String n(String s) { return s == null ? "" : s; }
}
