package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.ChatMessageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.MissedMessages;
import com.nakamas.hatfieldbackend.repositories.MessageRepository;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final TicketRepository ticketRepository;
    private final PhotoService photoService;
    private final Random random = new Random();

    public void createMessage(CreateChatMessage create) {
        User sender = userService.getUser(create.sender());
        Ticket ticket = ticketRepository.findById(create.ticketId()).orElseThrow(() -> new CustomException("Could not find ticket with such id"));
        ChatMessage message = new ChatMessage(create, sender);
        if (create.receiver() != null && create.publicMessage())
            message.setReceiver(userService.getUser(create.receiver()));
        ChatMessage save = messageRepository.save(message);
        ChatMessageView response = new ChatMessageView(save);
        if (save.getSender().getRole().equals(UserRole.CLIENT) ^ save.getReceiver() == null) {
            sendChatMessageToShop(ticket.getShop(), response);
        } else if (save.getReceiver() != null) {
            sendChatMessageToUser(save.getReceiver().getId().toString(), response);
        }
    }

    @Transactional()
    public void markMessageAsSeen(ChatMessage chatMessage) {
        chatMessage.setReadByReceiver(ZonedDateTime.now());
        messageRepository.save(chatMessage);
    }

    private void markMessagesAsSeen(UUID userId, Page<ChatMessage> allByTicket) {
        for (ChatMessage chatMessage : allByTicket.getContent()) {
            if (chatMessage.getReceiver() != null &&
                    userId.equals(chatMessage.getReceiver().getId()) &&
                    chatMessage.getReadByReceiver() == null)
                markMessageAsSeen(chatMessage);
        }
    }


    public void sendChatMessageToUser(String userId, ChatMessageView message) {
        messagingTemplate.convertAndSendToUser(userId, "/chat", message, createHeaders(userId));
    }

    public void sendChatMessageToShop(Shop shop, ChatMessageView message) {
        UserFilter filter = new UserFilter();
        filter.setShopId(shop.getId());
        filter.setRoles(List.of(UserRole.ENGINEER, UserRole.SALESMAN, UserRole.ADMIN));
        List<User> users = userService.getAll(filter);
        for (User user : users) {
            sendChatMessageToUser(user.getId().toString(), message);
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


    /**
     * Allows the WORKERS of the shop to contact each other.
     * about the ticket without having to notify the client
     *
     * @param ticketId = the currently open ticket
     * @return A list containing the outgoing chat message view
     * @see ChatMessageView
     */
    public PageView<ChatMessageView> getChatMessagesByTicketId(User user, Long ticketId, PageRequest pageRequest) {
        Page<ChatMessage> allByTicket = messageRepository.findAllByTicket(ticketId, pageRequest);
        markMessagesAsSeen(user.getId(), allByTicket);
        return new PageView<>(allByTicket.map(ChatMessageView::new));
    }

    /**
     * Shows the messages the client is supposed to see.
     *
     * @param userId = currently logged in CLIENT id
     * @return A list containing the outgoing chat message view
     * @see ChatMessageView
     */
    public PageView<ChatMessageView> getChatMessagesForClientByTicket(UUID userId, Long ticketId, PageRequest pageRequest) {
        Page<ChatMessage> allForClient = messageRepository.findAllForClient(userId, ticketId, pageRequest);
        markMessagesAsSeen(userId, allForClient);
        return new PageView<>(allForClient.map(ChatMessageView::new));
    }

    public void createImageMessage(MultipartFile file, Long ticketId, Boolean publicMessage, User sender) {
        Photo save = photoService.saveChatImage(sender.getFullName(), ticketId, file);
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new CustomException("Could not find ticket with id"));
        UUID receiverId = Objects.equals(ticket.getClient().getId(), sender.getId()) ? ticket.getCreatedBy().getId() : ticket.getClient().getId();
        CreateChatMessage chatMessage = new CreateChatMessage("image/" + save.getId(), ZonedDateTime.now(), sender.getId(), receiverId, ticket.getId(), true, publicMessage, random.nextLong());
        createMessage(chatMessage);
    }

    @Transactional(readOnly = true)
    public MissedMessages getNumberOfMissedMessages(User user) {
        int totalCount = 0;
        Map<Long, Integer> missedMessages = new HashMap<>();

        List<Ticket> tickets = user.getRole().equals(UserRole.CLIENT) ? ticketRepository.findAllForClient(user.getId()) : ticketRepository.findAllForShop(user.getShop().getId());
        for (Ticket clientTicket : tickets) {
            int count = user.getRole().equals(UserRole.CLIENT) ? messageRepository.getMissedMessagesCountForTicket(user.getId(), clientTicket.getId()) : messageRepository.getMissedMessagesCountForTicket(clientTicket.getId());
            missedMessages.put(clientTicket.getId(), count);
            totalCount += count;
        }
        return new MissedMessages(missedMessages, totalCount);
    }
}
