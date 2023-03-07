package com.nakamas.hatfieldbackend.models.views.incoming;

import java.util.Map;

public record CreateInventoryItem(
        Long modelId,
        String model,
        Long brandId,
        String brand,
        Integer count,
        Long shopId,
        Long categoryId,
        Map<String, String> properties) {
}
