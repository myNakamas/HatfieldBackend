package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.SoldItem;

import java.time.ZonedDateTime;

public record SoldItemView(InventoryItemView item, Integer soldCount, ZonedDateTime timestamp)
{
    public SoldItemView(SoldItem soldItem) {
        this(new InventoryItemView(soldItem.getItem()),soldItem.getSoldCount(),soldItem.getTimestamp());
    }
}
