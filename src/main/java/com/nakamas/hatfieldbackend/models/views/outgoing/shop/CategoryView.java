package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.enums.ItemType;

import java.util.List;

public record CategoryView(Long id, String name, ItemType itemType, List<CategoryColumnView> columns) {

    public CategoryView(Category category) {
        this(category.getId(), category.getName(), category.getType(), category.getFields().stream().map(CategoryColumnView::new).toList());
    }
}
