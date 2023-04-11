package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.PaymentMethod;
import com.nakamas.hatfieldbackend.models.enums.WarrantyPeriod;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Invoice extends AbstractPersistable<Long> {
    @Enumerated
    private InvoiceType type;
    private String deviceModel;
    private String deviceBrand;
    private String serialNumber;
    private LocalDateTime timestamp;
    private Long ticketId;
    private Integer count;
    @Column(columnDefinition = "text")
    private String notes;
    private BigDecimal totalPrice;
    @ManyToOne
    private User createdBy;
    @ManyToOne
    private User client;
    @Enumerated
    private PaymentMethod paymentMethod;
    @Enumerated
    private WarrantyPeriod warrantyPeriod;


    public Invoice(CreateInvoice invoiceView, User creator, User client) {
        if (invoiceView.getType() != null) this.type = invoiceView.getType();
        if (invoiceView.getDeviceModel() != null) this.deviceModel = invoiceView.getDeviceModel();
        if (invoiceView.getDeviceBrand() != null) this.deviceBrand = invoiceView.getDeviceBrand();
        if (invoiceView.getSerialNumber() != null || invoiceView.getSerialNumber().isBlank()) {
            this.serialNumber = invoiceView.getSerialNumber();
        } else {
            this.serialNumber = "-";
        }
        this.ticketId = invoiceView.getTicketId();
        this.timestamp = LocalDateTime.now();
        if (invoiceView.getTicketId() != null) this.ticketId = invoiceView.getTicketId();
        if (invoiceView.getCount() != null) {
            this.count = invoiceView.getCount();
        } else {
            this.count = 1;
        }
        if (invoiceView.getNotes() != null) this.notes = invoiceView.getNotes();
        if (invoiceView.getTotalPrice() != null) this.totalPrice = invoiceView.getTotalPrice();
        if (invoiceView.getCreatedBy() != null) this.createdBy = creator;
        if (invoiceView.getClient() != null) this.client = client;
        if (invoiceView.getPaymentMethod() != null) this.paymentMethod = invoiceView.getPaymentMethod();
        if (invoiceView.getWarrantyPeriod() != null) {
            this.warrantyPeriod = invoiceView.getWarrantyPeriod();
        } else {
            this.warrantyPeriod = WarrantyPeriod.NONE;
        }
    }
}
