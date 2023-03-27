package com.nakamas.hatfieldbackend.models.views.outgoing;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.time.LocalDateTime;

//todo: add usedPart and soldItem when time comes
public record LogView(String action, UserProfile user, LocalDateTime timestamp, Long ticketId) {
    public LogView(Log log, UserProfile user) {
        this(log.getAction(), user, log.getTimestamp(), log.getTicketId());
    }
}
