package com.nakamas.hatfieldbackend.services.communication.sms.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public class SMSApiErrorException extends RuntimeException {
    @Setter
    private HttpStatus status;
    private final SMSApiErrorDetail detail;

    public SMSApiErrorException() {
        this.detail = new SMSApiErrorDetail();
    }

    public SMSApiErrorException(String message, SMSApiErrorDetail detail) {
        super(message);
        this.detail = detail;
    }

    public SMSApiErrorException(SMSApiErrorDetail detail) {
        this.detail = detail;
    }

    public record SMSApiErrorDetail(SMSApiErrors code, String message) {
        public SMSApiErrorDetail() {
            this(null, "Unknown");
        }

        public SMSApiErrorDetail(SMSApiErrors code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public enum SMSApiErrors {
        ACCESS_TOKEN_SIGNATURE_VERIFICATION_FAILED,
        APPLICATION_NOT_EXISTS,
        ACCESS_TOKEN_EXPIRED,
        MESSAGE_LOG_NOT_EXISTS
    }
}
