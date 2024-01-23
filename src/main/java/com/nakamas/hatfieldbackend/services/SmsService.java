package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.services.communication.sms.api.SmsClient;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsApiResponse;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsMessage;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

import static com.nakamas.hatfieldbackend.util.JwtUtil.prepareBearerToken;

@Slf4j
@Service
public class SmsService {
    private final TemplateEngine templateEngine;
    private final SmsClient smsClient;

    public SmsService(SmsClient smsClient) {
        this.smsClient = smsClient;
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("TEXT");
        templateEngine.setTemplateResolver(templateResolver);
    }

    public boolean sendSms(User client, String template, Context context) {
        String messageBody = createMessageBody(template, context);
        if (isSmsEnabled(client) && !client.getPhones().isEmpty()) {
            String phone = client.getPhones().get(0).getPhoneWithCode();
            String smsApiKey = client.getShop().getSettings().getSmsApiKey();
            postSendSmsMessage(phone, messageBody, prepareBearerToken(smsApiKey));
            log.info("Sending an SMS to client '{}' with phone num: '{}'", client.getFullName(), phone);
            return true;
        } else return false;
    }

    public String createMessageBody(String template, Context context) {
        return templateEngine.process("templates/sms/" + template, context);
    }

    @Async
    protected void postSendSmsMessage(String phone, String messageBody, String smsApiKey) {
        SmsMessage message = new SmsMessage(List.of(phone), messageBody);
        SmsApiResponse smsApiResponse = smsClient.sendMessage(new SmsRequestBody(List.of(message)), smsApiKey);
        log.info("Response from SMS api: " + smsApiResponse);
    }

    public boolean isSmsEnabled(User user) {
        if (user == null) return false;
        return user.isSMSEnabled() && user.getShop().getSettings().isSmsEnabled();
    }

}
