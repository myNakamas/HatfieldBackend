package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record InvoiceView(Long id, String type,  String deviceName, String serialNumber,
                          ZonedDateTime timestamp,
                          String notes, BigDecimal totalPrice, UserProfile createdBy, UserProfile client,
                          String paymentMethod,
                          String warrantyPeriod,
                          ZonedDateTime warrantyLeft,
                          Long ticketId,
                          Boolean valid) {

    public InvoiceView(Invoice invoice) {
        this(invoice.getId(),
                invoice.getType().toString(),
                invoice.getDeviceName(),
                invoice.getSerialNumber(),
                invoice.getTimestamp(),
                invoice.getNotes(),
                invoice.getTotalPrice(),
                new UserProfile(invoice.getCreatedBy()),
                invoice.getClient() != null ? new UserProfile(invoice.getClient()) : null,
                invoice.getPaymentMethod().toString(),
                invoice.getWarrantyPeriod().toString(),
                invoice.getTimestamp().plus(invoice.getWarrantyPeriod().getPeriod()),
                invoice.getTicketId(),
                invoice.isValid());
    }

}