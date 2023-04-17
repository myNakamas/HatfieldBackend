package com.nakamas.hatfieldbackend.models.views.incoming;

import java.math.BigDecimal;
import java.util.Map;

public record CreateInventoryItem(
        Long id,
        String name,
        BigDecimal price, String model, Long modelId,
        String brand, Long brandId,
        Integer count,
        Long shopId,
        Long categoryId,
        Map<String, String> properties) {
}
