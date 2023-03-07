package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;

import java.util.Map;

public record InventoryItemView(Long id, String model, String brand, int count, Long shopId, CategoryView categoryView,
                                Map<String, String> columns) {
    public InventoryItemView(InventoryItem item) {
        this(item.getId(), item.getModel().getModel(), item.getBrand().getBrand(), item.getCount(), item.getShop().getId(), new CategoryView(item.getCategory()), item.getOtherProperties());
    }
}
