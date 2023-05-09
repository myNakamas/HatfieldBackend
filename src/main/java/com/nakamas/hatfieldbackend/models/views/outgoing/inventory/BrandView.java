package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;

import java.util.List;

public record BrandView(Long id, String value, List<ItemPropertyView> models) {
    public BrandView(Brand b) {
        this(b.getId(), b.getBrand(), b.getModels().stream().map(ItemPropertyView::new).toList());
    }
}
