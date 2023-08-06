package com.nakamas.hatfieldbackend.services.communication.sms.models;

import java.time.LocalDateTime;

public record SmsApiResponse(String request_id, String status, LocalDateTime created_at) {
}
