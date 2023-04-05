package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;

public record ShortItemView(Long id, String model, String brand, int count) {
    public ShortItemView(InventoryItem item) {
        this(item.getId(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount());
    }
}
