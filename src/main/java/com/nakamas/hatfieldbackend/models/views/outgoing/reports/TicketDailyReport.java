package com.nakamas.hatfieldbackend.models.views.outgoing.reports;

import java.time.LocalDate;

public record TicketDailyReport(LocalDate date, int created, int completed) {
}
