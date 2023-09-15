package com.nakamas.hatfieldbackend.services.communication.sms.models;

import java.util.List;

public record SmsMessage(String channel, List<String> recipients, String content, String msg_type, String data_coding) {
    public SmsMessage(List<String> recipients, String content) {
        this("sms",recipients,content,"text","text");
    }
}