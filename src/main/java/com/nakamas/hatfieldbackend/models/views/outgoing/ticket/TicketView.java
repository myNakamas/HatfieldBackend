package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserLogin;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
        ZonedDateTime timestamp,
        ZonedDateTime deadline,
        String notes,
        TicketStatus status,
        BigDecimal totalPrice,
        BigDecimal deposit,
        UserLogin createdBy,
        UserLogin client,
        List<UsedPartView> usedParts,
        InvoiceView invoice
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
                new UserLogin(ticket.getCreatedBy()),
                (ticket.getClient() != null) ? new UserLogin(ticket.getClient()) : null,
                ticket.getUsedParts().stream().map(UsedPartView::new).toList(),
                ticket.getInvoices().stream().filter(Invoice::isTicketInvoice).findFirst().map(InvoiceView::new).orElse(null));
    }
}
