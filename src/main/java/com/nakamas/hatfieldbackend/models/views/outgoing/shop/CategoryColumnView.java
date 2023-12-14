package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.CategoryColumn;

public record CategoryColumnView(String name,
                                 Boolean showOnDocument,
                                 Boolean showNameOnDocument) {
    public CategoryColumnView(CategoryColumn column) {
        this(column.getName(), column.getShowOnDocument(), column.getShowNameOnDocument());
    }
}
