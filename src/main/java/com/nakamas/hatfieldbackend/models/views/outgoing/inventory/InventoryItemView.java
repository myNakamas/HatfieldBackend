package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;

import java.math.BigDecimal;
import java.util.Map;

public record InventoryItemView(Long id, String name, BigDecimal price, String model, String brand, int count,
                                RequiredItemView requiredItem, Long shopId,
                                CategoryView categoryView,
                                Map<String, String> columns) {
    public InventoryItemView(InventoryItem item, CategoryView categoryView) {
        this(item.getId(),
                item.getName(),
                item.getPrice(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount(),
                new RequiredItemView(item.getRequiredItem()),
                item.getShop().getId(),
                categoryView,
                item.getOtherProperties());
    }
    public InventoryItemView(InventoryItem item) {
        this(item.getId(),
                item.getName(),
                item.getPrice(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount(),
                new RequiredItemView(item.getRequiredItem()),
                item.getShop().getId(),
                new CategoryView(item.getCategoryId(), null, null, null),
                item.getOtherProperties());
    }
}
