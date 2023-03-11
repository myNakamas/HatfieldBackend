package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageView(
        Long id,
        String text,
        LocalDateTime timestamp,
        LocalDateTime readByReceiver,
        UUID sender,
        UUID receiver,
        Long ticketId
) {

    public ChatMessageView(ChatMessage message) {
        this(message.getId(), message.getText(), message.getTimestamp(), message.getReadByReceiver(), message.getSender().getId(), message.getReceiver().getId(), message.getTicket_id());
    }
}
