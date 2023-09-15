package com.nakamas.hatfieldbackend.services.communication.sms.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class SMSApiValidationErrorException extends RuntimeException {
    private final List<SMSApiValidationValidationErrorDetail> detail;

    public SMSApiValidationErrorException() {
        this.detail = new ArrayList<>();
    }

    public SMSApiValidationErrorException(String message, List<SMSApiValidationValidationErrorDetail> detail) {
        super(message);
        this.detail = detail;
    }

    public record SMSApiValidationValidationErrorDetail(List<String> loc, String msg, String type) {
    }
}
