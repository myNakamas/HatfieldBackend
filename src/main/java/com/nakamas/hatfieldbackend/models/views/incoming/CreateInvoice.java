package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.PaymentMethod;
import com.nakamas.hatfieldbackend.models.enums.WarrantyPeriod;

import java.math.BigDecimal;

public record CreateInvoice(InvoiceType type, Model deviceModel, Brand deviceBrand, String serialNumber,
                            String notes, BigDecimal totalPrice, User createdBy, User client, PaymentMethod paymentMethod,
                            WarrantyPeriod warranty) {
}
