package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void createMessage(CreateChatMessage message) {
        messageService.createMessage(message);
    }

}
