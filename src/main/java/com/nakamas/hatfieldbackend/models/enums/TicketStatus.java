package com.nakamas.hatfieldbackend.models.enums;

/**
 * PENDING,STARTED - Ticket is active (shown as to be worked on RIGHT NOW)
 * FINISHED,UNFIXABLE - Notify client with according msg/email
 */
public enum TicketStatus {
    PENDING,
    STARTED,

    DIAGNOSED,
    WAITING_FOR_PARTS,
    ON_HOLD,
    EXPECTING_ARRIVAL,

    FINISHED,
    UNFIXABLE,

    CANCELLED_BY_CLIENT,
    SHIPPED_TO_CUSTOMER,
    COLLECTED;

    public boolean isActive() {
        return this == PENDING || this == DIAGNOSED || this == STARTED;
    }
}
