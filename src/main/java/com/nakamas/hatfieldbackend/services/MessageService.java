package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.UserChats;
import com.nakamas.hatfieldbackend.repositories.MessageRepository;
import com.nakamas.hatfieldbackend.repositories.PhotoRepository;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final TicketRepository ticketRepository;
    private final PhotoRepository photoRepository;
    private final Random random = new Random();

    public void createMessage(CreateChatMessage create) {
        User sender = userService.getUser(create.sender());
        Ticket ticket = ticketRepository.findById(create.ticketId()).orElseThrow(() -> new CustomException("Could not find ticket with such id"));
        ChatMessage message = new ChatMessage(create, sender);
        if (create.receiver() != null && create.publicMessage())
            message.setReceiver(userService.getUser(create.receiver()));
        ChatMessage save = messageRepository.save(message);
        ChatMessageView response = new ChatMessageView(save);

        if (save.getSender().getRole().equals(UserRole.CLIENT)) {
            sendChatMessageToUser(ticket.getCreatedBy().getId().toString(), response);
        } else if (save.getReceiver() != null) {
            sendChatMessageToUser(save.getReceiver().getId().toString(), response);
        }

    }

    @Transactional
    public void markMessageAsSeen(Long messageId) {
        ChatMessage byId = messageRepository.findById(messageId).orElseThrow(() -> new CustomException("Cannot find message with id"));
        byId.setReadByReceiver(ZonedDateTime.now());
        ChatMessage save = messageRepository.save(byId);
        sendMessageSeenToUser(Objects.requireNonNull(save.getSender().getId()).toString(), new ChatMessageView(save));
    }

    public void sendChatMessageToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/chat", message, createHeaders(userId));
    }

    public void sendMessageSeenToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/seen", message, createHeaders(userId));
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
    public List<ChatMessageView> getChatMessagesForClientByTicket(UUID userId, Long ticketId) {
        return messageRepository.findAllForClient(userId, ticketId).stream().map(ChatMessageView::new).toList();
    }

    public void createImageMessage(MultipartFile file, Long ticketId, Boolean publicMessage, User sender) {
        try {
            Photo photo = new Photo(file.getBytes(), false);
            Photo save = photoRepository.save(photo);
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new CustomException("Could not find ticket with id"));
            UUID receiverId = Objects.equals(ticket.getClient().getId(), sender.getId()) ? ticket.getCreatedBy().getId() : ticket.getClient().getId();
            CreateChatMessage chatMessage = new CreateChatMessage("image/" + save.getId(), ZonedDateTime.now(), sender.getId(), receiverId, ticket.getId(), true, publicMessage, random.nextLong());
            createMessage(chatMessage);
        } catch (IOException e) {
            throw new CustomException("Please try again later");
        }
    }
}
