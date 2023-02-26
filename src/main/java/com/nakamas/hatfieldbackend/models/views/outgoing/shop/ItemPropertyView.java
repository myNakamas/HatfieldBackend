package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;

public record ItemPropertyView(Long id, String value) {
    public ItemPropertyView(Model m) {
        this(m.getId(), m.getModel());
    }

    public ItemPropertyView(Brand b) {
        this(b.getId(), b.getBrand());
    }
}
