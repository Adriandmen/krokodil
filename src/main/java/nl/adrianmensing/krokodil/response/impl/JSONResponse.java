package nl.adrianmensing.krokodil.response.impl;

import nl.adrianmensing.krokodil.logic.Entity;
import nl.adrianmensing.krokodil.response.BasicResponse;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.utils.result.Result;
import nl.adrianmensing.krokodil.utils.result.Success;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class JSONResponse<T> implements BasicResponse<T> {

    private final T value;
    private final HttpStatus status;

    public JSONResponse(T value, HttpStatus status) {
        this.value = value;
        this.status = status;
    }

    public T value() {
        return value;
    }

    public HttpStatus status() {
        return status;
    }

    public JSONResponse(T value) {
        this(value, defaultStatus);
    }

    @Override
    public Result<T> result() {
        return new Success<>(value);
    }

    @Override
    public Response<T> update(Entity entity) {
        return new ErrorResponse<>("Cannot update JSON response", HttpStatus.BAD_REQUEST);
    }

    @Override
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }
}
