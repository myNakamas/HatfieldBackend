package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.enums.ItemType;

public record CreateInventoryItem(
        Long modelId,
        Long brandId,
        Integer count,
        Long shopId,
        ItemType type) {
}
