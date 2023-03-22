package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;

import java.util.Map;

public record InventoryItemView(Long id, String model, String brand, int count, Boolean needed, Long shopId,
                                CategoryView categoryView,
                                Map<String, String> columns) {
    public InventoryItemView(InventoryItem item) {
        this(item.getId(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount(),
                item.getShoppingListNeeded(),
                item.getShop().getId(),
                new CategoryView(item.getCategory()), item.getOtherProperties());
    }
}
