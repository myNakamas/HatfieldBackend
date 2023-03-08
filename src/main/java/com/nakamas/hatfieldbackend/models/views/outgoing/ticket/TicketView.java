package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketView(String deviceModel,
                         String deviceBrand,
                         String customerRequest,
                         String problemExplanation,
                         String deviceCondition,
                         String devicePassword,
                         String serialNumberOrImei,
                         String accessories,
                         LocalDateTime timestamp,
                         LocalDateTime deadline,
                         String notes,
                         TicketStatus status,
                         BigDecimal totalPrice,
                         BigDecimal deposit,
                         Integer priority
) {
    public TicketView(Ticket ticket) {
        this(ticket.getDeviceModel().getModel(),
                ticket.getDeviceBrand().getBrand(),
                ticket.getCustomerRequest(),
                ticket.getDeviceProblemExplanation(),
                ticket.getDeviceCondition(),
                ticket.getDevicePassword(),
                ticket.getSerialNumberOrImei(),
                ticket.getAccessories(),
                ticket.getTimestamp(),
                ticket.getDeadline(),
                ticket.getNotes(),
                ticket.getStatus(),
                ticket.getTotalPrice(),
                ticket.getDeposit(),
                ticket.getPriority());
    }
}
