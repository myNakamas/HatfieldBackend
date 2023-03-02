package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Greeting;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/websocket")
    @SendTo("/websocket")
    public Greeting greeting(ChatMessage message) throws Exception {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getText()) + "!");
    }
}
