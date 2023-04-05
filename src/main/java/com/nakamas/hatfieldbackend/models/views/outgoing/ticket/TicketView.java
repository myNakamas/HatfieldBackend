package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TicketView(
        Long id,
        String deviceModel,
        String deviceBrand,
        String deviceLocation,
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
        UserProfile createdBy,
        UserProfile client,
        Integer priority,
        List<UsedPartView> usedParts
) {
    public TicketView(Ticket ticket) {
        this(ticket.getId(),
                ticket.getDeviceModel() != null ? ticket.getDeviceModel().getModel() : null,
                ticket.getDeviceBrand() != null ? ticket.getDeviceBrand().getBrand() : null,
                ticket.getDeviceLocation() != null ? ticket.getDeviceLocation().getLocation() : null,
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
                new UserProfile(ticket.getCreatedBy()),
                (ticket.getClient() != null) ? new UserProfile(ticket.getClient()) : null,
                ticket.getPriority(),
                ticket.getUsedParts().stream().map(UsedPartView::new).toList());
    }
}
