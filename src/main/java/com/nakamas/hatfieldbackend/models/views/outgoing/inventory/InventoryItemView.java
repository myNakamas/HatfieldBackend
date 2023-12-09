package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;

import java.math.BigDecimal;
import java.util.Map;

public record InventoryItemView(Long id, String name, String imei, BigDecimal purchasePrice, BigDecimal sellPrice, String model,
                                String brand, int count,
                                RequiredItemView requiredItem, Long shopId,
                                CategoryView categoryView,
                                int missingCount,
                                Map<String, String> columns) {
    public InventoryItemView(InventoryItem item, CategoryView categoryView) {
        this(item.getId(),
                item.getName(),
                item.getImei(),
                item.getPurchasePrice(),
                item.getSellPrice(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount(),
                new RequiredItemView(item.getRequiredItem()),
                item.getShop().getId(),
                categoryView,
                item.getMissingCount(),
                item.getOtherProperties());
    }
    public InventoryItemView(InventoryItem item) {
        this(item.getId(),
                item.getName(),
                item.getImei(),
                item.getPurchasePrice(),
                item.getSellPrice(),
                item.getModel() != null ? item.getModel().getModel() : null,
                item.getBrand() != null ? item.getBrand().getBrand() : null,
                item.getCount(),
                new RequiredItemView(item.getRequiredItem()),
                item.getShop().getId(),
                new CategoryView(item.getCategoryId(), null, null, null),
                item.getMissingCount(),
                item.getOtherProperties());
    }
}
