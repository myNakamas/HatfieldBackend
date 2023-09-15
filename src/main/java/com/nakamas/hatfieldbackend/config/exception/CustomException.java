package com.nakamas.hatfieldbackend.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final HttpStatus status;
//    todo: add unique error code and handle it in the app
//    private final int errorCode;

    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
//        this.errorCode = errorCode;
    }
    public CustomException(HttpStatus status,String message) {
        super(message);
        this.status = status;
    }
}
