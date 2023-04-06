package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatController {
    private final MessageService messageService;
    @GetMapping("all")
    public List<ChatMessageView> getAllMessagesForTicket(@RequestParam Long ticketId){
        return messageService.getChatMessagesByTicketId(ticketId);
    }

    @GetMapping("client/all")
    public List<ChatMessageView> getAllMessagesForClient(@RequestParam UUID userId){
        return messageService.getChatMessagesForClient(userId);
    }
}
