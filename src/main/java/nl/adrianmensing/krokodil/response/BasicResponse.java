package nl.adrianmensing.krokodil.response;

import org.springframework.http.HttpStatus;

public interface BasicResponse<T> extends Response<T> {

    HttpStatus defaultStatus = HttpStatus.OK;

}
