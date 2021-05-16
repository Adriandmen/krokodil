package nl.adrianmensing.krokodil.response.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public record ErrorContent(String errorMessage, HttpStatus status) {
    @NotNull
    public Map<Object, Object> contents() {
        Map<Object, Object> body = new HashMap<>();
        Map<Object, Object> errorStatus = new HashMap<>();

        errorStatus.put("reason", status.getReasonPhrase());
        errorStatus.put("code", status.value());

        body.put("error", errorMessage);
        body.put("status", errorStatus);

        return body;
    }
}
