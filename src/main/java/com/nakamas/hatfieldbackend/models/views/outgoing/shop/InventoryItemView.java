package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.enums.ItemType;

public record InventoryItemView(Long id, String model, String brand, int count, Long shopId, ItemType type) {
    public InventoryItemView(InventoryItem item) {
        this(item.getId(),item.getModel().getModel(),item.getBrand().getBrand(),item.getCount(),item.getShop().getId(),item.getType());
    }
}
