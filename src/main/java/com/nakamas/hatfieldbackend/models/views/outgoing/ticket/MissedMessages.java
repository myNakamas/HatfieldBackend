package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import java.util.Map;

public record MissedMessages(Map<Long, Integer> countPerTicket, Integer totalCount) {
}
