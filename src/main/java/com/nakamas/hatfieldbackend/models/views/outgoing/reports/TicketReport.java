package com.nakamas.hatfieldbackend.models.views.outgoing.reports;

import java.util.List;

public record TicketReport(int totalCount, List<TicketDailyReport> calendar) {
}

