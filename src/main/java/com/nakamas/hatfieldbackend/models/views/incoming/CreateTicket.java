package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateTicket(String deviceModel,
                           String deviceBrand,
                           String deviceLocation,
                           String customerRequest,
                           String problemExplanation,
                           String deviceCondition,
                           String devicePassword,
                           String serialNumberOrImei,
                           String accessories,
                           ZonedDateTime deadline,
                           ZonedDateTime timestamp,
                           String notes,
                           TicketStatus status,
                           BigDecimal totalPrice,
                           BigDecimal deposit,
                           UUID clientId) {
}
