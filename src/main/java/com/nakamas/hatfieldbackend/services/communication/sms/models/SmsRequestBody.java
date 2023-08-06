package com.nakamas.hatfieldbackend.services.communication.sms.models;

import java.util.List;

public record SmsRequestBody(List<SmsMessage> messages) {
}
