package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;

import java.time.LocalDateTime;

public record UsedPartView(Long ticketId, InventoryItemView item, Integer usedCount, LocalDateTime timestamp)
{
    public UsedPartView(UsedPart usedPart) {
        this(usedPart.getTicket().getId(),new InventoryItemView(usedPart.getItem()),usedPart.getUsedCount(),usedPart.getTimestamp());
    }
}