package com.nakamas.hatfieldbackend.models.views.incoming;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateChatMessage(
        String text,
        LocalDateTime timestamp,
        UUID sender,
        UUID receiver,
        Long ticketId) {
}
