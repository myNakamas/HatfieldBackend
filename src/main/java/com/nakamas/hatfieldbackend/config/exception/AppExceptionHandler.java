package com.nakamas.hatfieldbackend.config.exception;

import com.nakamas.hatfieldbackend.services.communication.sms.models.SMSApiErrorException;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SMSApiValidationErrorException;
import io.fusionauth.jwt.JWTExpiredException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomExceptions(CustomException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, ex.getStatus(), ex.getMessage());
        log.debug("Error handled:" + ex.getMessage());
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

    @ExceptionHandler({ForbiddenActionException.class})
    public ErrorResponse handleJWTExceptions(ForbiddenActionException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.FORBIDDEN, ex.getMessage());
        return responseBuilder.build();
    }

    @ExceptionHandler({FileSizeLimitExceededException.class})
    public ErrorResponse handleFileSizeExceptions(FileSizeLimitExceededException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        return responseBuilder.build();
    }

    @ExceptionHandler({MailException.class})
    public ErrorResponse handleMailException(MailException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error("MailException has been thrown. Message: {}", ex.getMessage());
        return responseBuilder.build();
    }

    @ExceptionHandler({SMSApiErrorException.class})
    public ErrorResponse handleSMSException(SMSApiErrorException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Our SMS services are currently down.");
        if (ex.getDetail().code() != null)
            log.error("Sms api returned error code `{}` with response `{}`", ex.getDetail().code(), ex.getDetail().message());
        return responseBuilder.build();
    }

    @ExceptionHandler({SMSApiValidationErrorException.class})
    public ErrorResponse handleSMSException(SMSApiValidationErrorException ex) {
        ErrorResponse.Builder responseBuilder = ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Our SMS services are currently down.");
        log.warn("Sms api returned validation error with response `{}`", ex.getDetail());
        return responseBuilder.build();
    }

}
