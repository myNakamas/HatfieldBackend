package com.nakamas.hatfieldbackend.models.views.outgoing;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.time.ZonedDateTime;

public record LogView(Long id, String action, UserProfile user, ZonedDateTime timestamp, Long ticketId, Long usedItemId,
                      Long itemSold) {
    public LogView(Log log, UserProfile user) {

        this(log.getId(), log.getAction(), user, log.getTimestamp(), log.getTicketId(), log.getPartUsedId(), log.getItemSoldId());
    }
}
