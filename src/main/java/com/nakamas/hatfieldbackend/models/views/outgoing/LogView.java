package com.nakamas.hatfieldbackend.models.views.outgoing;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.time.ZonedDateTime;

public record LogView(Long id, String action, LogType type, UserProfile user, ZonedDateTime timestamp, Long ticketId, Long itemId,
                      Long invoiceId, Long shopId) {
    public LogView(Log log, UserProfile user) {

        this(log.getId(), log.getAction(), log.getLogType(),user, log.getTimestamp(), log.getTicketId(), log.getItemId(), log.getInvoiceId(), log.getShopId());
    }
}
