package com.nakamas.hatfieldbackend.models.views.incoming;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateChatMessage(
        String text,
        ZonedDateTime timestamp,
        UUID sender,
        UUID receiver,
        Long ticketId,
        Long randomId) {
}
