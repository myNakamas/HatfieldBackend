package com.nakamas.hatfieldbackend.models.views.outgoing;

import java.math.BigDecimal;

public record PricingEvaluation(
        BigDecimal price,
        BigDecimal originalPrice,
        boolean priceExists,
        String action
) {

}