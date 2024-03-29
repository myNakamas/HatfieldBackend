package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ChatMessageView(
        Long id,
        String text,
        ZonedDateTime timestamp,
        ZonedDateTime readByReceiver,
        UUID sender,
        UUID receiver,
        Long ticketId,
        Boolean isImage,
        Long randomId
) {

    public ChatMessageView(ChatMessage message) {
        this(message.getId(), message.getText(), message.getTimestamp(), message.getReadByReceiver(), message.getSender().getId(), message.getReceiver() != null ? message.getReceiver().getId() : null, message.getTicketId(), message.getIsImage(), message.getRandomId());
    }
}
