package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.RequiredItem;

public record RequiredItemView(Integer requiredAmount, Boolean isNeeded) {
    public RequiredItemView(RequiredItem requiredItem) {
        this(requiredItem.getRequiredAmount(), requiredItem.getNeeded());
    }
}

