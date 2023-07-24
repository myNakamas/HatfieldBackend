package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chat")
public class ChatController {
    private final MessageService messageService;

    @GetMapping("all")
    public List<ChatMessageView> getAllMessagesForTicket(@RequestParam Long ticketId) {
        return messageService.getChatMessagesByTicketId(ticketId);
    }

    @GetMapping("client/all")
    public List<ChatMessageView> getAllMessagesForClient(@AuthenticationPrincipal User user, @RequestParam Long ticketId) {
        return messageService.getChatMessagesForClientByTicket(user.getId(), ticketId);
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void sendImageAsMessage(@AuthenticationPrincipal User user, @RequestParam Long ticketId, @RequestParam Boolean publicMessage, @RequestBody MultipartFile image) {
        messageService.createImageMessage(image, ticketId, publicMessage, user);
    }
}
