package nl.adrianmensing.krokodil.response.impl;

import nl.adrianmensing.krokodil.logic.Entity;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.utils.result.Failure;
import nl.adrianmensing.krokodil.utils.result.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ErrorResponse<T> implements Response<T> {

    private final String errorMessage;
    private final HttpStatus status;
    private final HttpHeaders headers;

    public String errorMessage() {
        return errorMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public ErrorResponse(String errorMessage, HttpStatus status, HttpHeaders headers) {
        this.errorMessage = errorMessage;
        this.status = status;
        this.headers = headers;
    }

    public ErrorResponse(HttpStatus status) {
        this(null, status, null);
    }

    public ErrorResponse(String errorMessage, HttpStatus status) {
        this(errorMessage, status, null);
    }

    @Override
    public Result<T> result() {
        return new Failure<>(errorMessage);
    }

    @Override
    public Response<T> update(Entity entity) {
        return this;
    }
}
