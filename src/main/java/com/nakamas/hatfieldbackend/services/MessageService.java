package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.UserChats;
import com.nakamas.hatfieldbackend.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createMessage(CreateChatMessage create) {
        User sender = userService.getUser(create.sender());
        ChatMessage message = new ChatMessage(create, sender);
        if (create.receiver() != null) message.setReceiver(userService.getUser(create.receiver()));
        ChatMessage save = messageRepository.save(message);
        ChatMessageView response = new ChatMessageView(save);
        if (save.getReceiver() != null && save.getReceiver().getId() != null)
            sendChatMessageToUser(save.getReceiver().getId().toString(), response);
        sendMessageSuccess(Objects.requireNonNull(sender.getId()).toString(), response);
    }

    @Transactional
    public void markMessageAsSeen(Long messageId) {
        ChatMessage byId = messageRepository.findById(messageId).orElseThrow(() -> new CustomException("Cannot find message with id"));
        byId.setReadByReceiver(LocalDateTime.now());
        ChatMessage save = messageRepository.save(byId);
        messageRepository.markReadByUser(save.getSender().getId(), save.getReceiver().getId(), save.getReadByReceiver());
        sendMessageSeenToUser(Objects.requireNonNull(save.getSender().getId()).toString(), new ChatMessageView(save));
    }

    public void sendChatMessageToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/chat", message, createHeaders(userId));
    }

    public void sendMessageSeenToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/seen", message, createHeaders(userId));
    }

    public void sendMessageSuccess(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/sent", message, createHeaders(userId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    public UserChats findAllByIds(UUID id, UUID userId) {
        List<ChatMessageView> sent = messageRepository.findAllSentToUser(id, userId).stream().map(ChatMessageView::new).toList();
        List<ChatMessageView> received = messageRepository.findAllReceivedFromUser(id, userId).stream().map(ChatMessageView::new).toList();
        return new UserChats(sent, received, userId);
    }

    public List<ChatMessageView> getChatMessages(UUID id, UUID userId) {
        return messageRepository.findAllByUsers(id, userId).stream().map(ChatMessageView::new).toList();
    }

    /**
     * Allows the WORKERS of the shop to contact each other.
     * about the ticket without having to notify the client
     *
     * @param ticketId = the currently open ticket
     * @return A list containing the outgoing chat message view
     * @see ChatMessageView
     */
    public List<ChatMessageView> getChatMessagesByTicketId(Long ticketId) {
        return messageRepository.findAllByTicket(ticketId).stream().map(ChatMessageView::new).toList();
    }

    /**
     * Shows the messages the client is supposed to see.
     *
     * @param userId = currently logged in CLIENT id
     * @return A list containing the outgoing chat message view
     * @see ChatMessageView
     */
    public List<ChatMessageView> getChatMessagesForClient(UUID userId) {
        return messageRepository.findAllForClient(userId).stream().map(ChatMessageView::new).toList();
    }
}
