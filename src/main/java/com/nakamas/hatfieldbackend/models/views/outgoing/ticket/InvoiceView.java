package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record InvoiceView(Long id, String type, String deviceModel, String deviceBrand, String serialNumber,
                          ZonedDateTime timestamp,
                          String notes, BigDecimal totalPrice, UserProfile createdBy, UserProfile client, String paymentMethod,
                          String warrantyPeriod) {

    public InvoiceView(Invoice invoice) {
        this(invoice.getId(),invoice.getType().toString(), invoice.getDeviceModel().getModel(), invoice.getDeviceBrand().getBrand(),
                invoice.getSerialNumber(), invoice.getTimestamp(), invoice.getNotes(), invoice.getTotalPrice(),
                new UserProfile(invoice.getCreatedBy()), new UserProfile(invoice.getClient()), invoice.getPaymentMethod().toString(), invoice.getWarrantyPeriod().toString());
    }

}