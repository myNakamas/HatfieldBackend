package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.RequiredItem;
import com.nakamas.hatfieldbackend.models.enums.RequiredItemStatus;
import com.nakamas.hatfieldbackend.models.enums.RequiredReason;

public record RequiredItemView(Integer requiredAmount, RequiredReason requiredReason, RequiredItemStatus status) {
    public RequiredItemView(RequiredItem requiredItem) {
        this(requiredItem.getRequiredAmount(), requiredItem.getReason(), requiredItem.getStatus());
    }
}

