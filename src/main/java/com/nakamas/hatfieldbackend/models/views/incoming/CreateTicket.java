package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                           LocalDateTime deadline,
                           String notes,
                           TicketStatus status,
                           BigDecimal totalPrice,
                           BigDecimal deposit,
                           UUID clientId,
                           Integer priority) {

}
