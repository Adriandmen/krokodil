package nl.adrianmensing.krokodil.response.impl;

import nl.adrianmensing.krokodil.logic.Entity;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.utils.result.Failure;
import nl.adrianmensing.krokodil.utils.result.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public record ErrorResponse<T>(String errorMessage, HttpStatus status, HttpHeaders headers) implements Response<T> {

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
