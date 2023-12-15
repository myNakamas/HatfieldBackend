package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.shop.CategoryColumn;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateInventoryItem(
        Long id,
        String name,
        String imei,
        BigDecimal purchasePrice,
        BigDecimal sellPrice,
        String model, Long modelId,
        String brand, Long brandId,
        Integer count,
        Long shopId,
        Long categoryId,
        Map<String, String> properties) {
    public void filterProperties(List<CategoryColumn> allowedProperties) {
        properties.entrySet().removeIf(property ->
                allowedProperties.stream().noneMatch(ap ->
                        ap.getName().equals(property.getKey())
                )
        );
    }
}
