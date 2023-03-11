package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageView createMessage(CreateChatMessage create) {
        User sender = userService.getUser(create.sender());
        User receiver = userService.getUser(create.receiver());
        ChatMessage message = new ChatMessage(create, sender, receiver);
        ChatMessage save = messageRepository.save(message);
        ChatMessageView response = new ChatMessageView(save);
        sendChatMessageToUser(Objects.requireNonNull(receiver.getId()).toString(), response);
        return response;
    }

    public void sendChatMessageToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/chat", message, createHeaders(userId));
        log.info("Sending message: Message sent to user " + userId);
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
