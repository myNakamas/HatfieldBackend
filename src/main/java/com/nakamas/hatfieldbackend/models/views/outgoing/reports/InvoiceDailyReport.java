package com.nakamas.hatfieldbackend.models.views.outgoing.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceDailyReport(LocalDate date, BigDecimal dailyIncome, int count) {
}
