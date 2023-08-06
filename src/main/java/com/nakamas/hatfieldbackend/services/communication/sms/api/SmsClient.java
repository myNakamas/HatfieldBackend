package com.nakamas.hatfieldbackend.services.communication.sms.api;


import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsApiResponse;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsRequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        url = "messages/v1",
        accept = MediaType.APPLICATION_JSON_VALUE)
public interface SmsClient {
    @PostExchange("send")
    SmsApiResponse sendMessage(@RequestBody SmsRequestBody body, @RequestHeader("Authorization") String token);

}
