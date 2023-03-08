package com.nakamas.hatfieldbackend.models.views.incoming;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTicket(String deviceModel,
                           String deviceBrand,
                           String customerRequest,
                           String problemExplanation,
                           String deviceCondition,
                           String devicePassword,
                           String serialNumberOrImei,
                           String accessories,
                           @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                           LocalDateTime deadline,
                           String notes,
                           TicketStatus status,
                           BigDecimal totalPrice,
                           BigDecimal deposit,
                           UUID clientId,
                           Integer priority) {

}
