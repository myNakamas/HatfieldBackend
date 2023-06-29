package com.nakamas.hatfieldbackend.models.views.outgoing.reports;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceReport(int totalCount, BigDecimal totalAmount, List<InvoiceDailyReport> calendar) {
}

