package com.nakamas.hatfieldbackend.config.api;

import com.nakamas.hatfieldbackend.services.communication.sms.api.SmsClient;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SMSApiErrorException;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SMSApiValidationErrorException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public SmsClient smsClient() {
//        todo: maybe implement error handling
        WebClient client = WebClient.builder()
                .defaultStatusHandler(code-> code.value()==401, resp -> resp.bodyToMono(SMSApiErrorException.class))
                .defaultStatusHandler(code-> code.value()==422, resp -> resp.bodyToMono(SMSApiValidationErrorException.class))
                .baseUrl("https://api.d7networks.com/")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

        return factory.createClient(SmsClient.class);
    }
}
