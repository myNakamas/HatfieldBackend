package com.nakamas.hatfieldbackend.config.exception;

import io.fusionauth.jwt.JWTExpiredException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomExceptions(CustomException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, ex.getStatus(), ex.getMessage());
        log.warn("Error handled:" + ex.getMessage());
        return responseBuilder.build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleValidationExceptions(ConstraintViolationException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            responseBuilder.property(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        return responseBuilder.build();
    }

    @ExceptionHandler({JWTExpiredException.class})
    public ErrorResponse handleJWTExceptions(JWTExpiredException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage());
        return responseBuilder.build();
    }
}
