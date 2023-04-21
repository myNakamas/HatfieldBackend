package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;

public record ShortItemView(Long id, String name, String model, String brand, int count) {
    public ShortItemView(InventoryItem item) {
        this(item.getId(),
                item.getName(),
                item.getModelString(),
                item.getBrandString(),
                item.getCount());
    }
}
