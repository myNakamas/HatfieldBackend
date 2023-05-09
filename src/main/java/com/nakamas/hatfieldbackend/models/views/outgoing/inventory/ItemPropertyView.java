package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;

public record ItemPropertyView(Long id, String value) {
    public ItemPropertyView(Model m) {
        this(m.getId(), m.getModel());
    }

    public ItemPropertyView(DeviceLocation d) {
        this(d.getId(), d.getLocation());
    }
}
