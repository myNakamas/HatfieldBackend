package com.nakamas.hatfieldbackend.models.views.outgoing;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.time.ZonedDateTime;

//todo: add usedPart and soldItem when time comes
public record LogView(String action, UserProfile user, ZonedDateTime timestamp, Long ticketId) {
    public LogView(Log log, UserProfile user) {
        this(log.getAction(), user, log.getTimestamp(), log.getTicketId());
    }
}
