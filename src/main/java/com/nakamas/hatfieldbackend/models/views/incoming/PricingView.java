package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.prices.Pricing;

import java.math.BigDecimal;

public record PricingView(
        Long id,
        String deviceType,
        String brand,
        String model,
        String issue,
        BigDecimal price,
        BigDecimal originalPrice) {

    public PricingView(Pricing p, String brand, String model){
        this(p.getId(), p.getDeviceType(), brand, model, p.getIssue(), p.getPrice(), p.getOriginalPrice());
    }
}
