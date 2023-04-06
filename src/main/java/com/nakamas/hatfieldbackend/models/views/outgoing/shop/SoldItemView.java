package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.SoldItem;

import java.time.LocalDateTime;

public record SoldItemView(InventoryItemView item, Integer soldCount, LocalDateTime timestamp)
{
    public SoldItemView(SoldItem soldItem) {
        this(new InventoryItemView(soldItem.getItem()),soldItem.getSoldCount(),soldItem.getTimestamp());
    }
}
