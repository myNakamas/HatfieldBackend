package com.nakamas.hatfieldbackend.models.views.outgoing.reports;

import java.math.BigDecimal;
import java.util.Map;

public record SellReport(BigDecimal totalAmount, Map<String, Integer> leaderboard) {
}
