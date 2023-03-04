package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Greeting;
import com.nakamas.hatfieldbackend.services.WebSocketSessionsHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequiredArgsConstructor
public class GreetingController {
    private final WebSocketSessionsHolder sessionsHolder;

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Greeting greeting(ChatMessage message) {
        System.out.println(message.getText());
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getText()) + "!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        sessionsHolder.remove(sessionId);
    }
}
