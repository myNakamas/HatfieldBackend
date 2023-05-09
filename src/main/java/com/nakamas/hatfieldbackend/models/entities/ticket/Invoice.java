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
import java.time.ZonedDateTime;

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
    private Long ticketId;
    private ZonedDateTime timestamp;
    private Integer count;
    @Column (columnDefinition = "text")
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
    private Boolean valid;


    public Invoice(CreateInvoice invoiceView, User creator, User client) {
        if (invoiceView.getType() != null) this.type = invoiceView.getType();
        if (invoiceView.getDeviceModel() != null) this.deviceModel = invoiceView.getDeviceModel();
        if (invoiceView.getDeviceBrand() != null) this.deviceBrand = invoiceView.getDeviceBrand();
        if (invoiceView.getSerialNumber() != null && invoiceView.getSerialNumber().isBlank()) {
            this.serialNumber = invoiceView.getSerialNumber();
        } else {
            this.serialNumber = "-";
        }
        this.ticketId = invoiceView.getTicketId();
        this.timestamp = ZonedDateTime.now();
        this.valid = true;
        if (invoiceView.getTicketId() != null) this.ticketId = invoiceView.getTicketId();
        if (invoiceView.getCount() != null) {
            this.count = invoiceView.getCount();
        } else {
            this.count = 1;
        }
        if (invoiceView.getNotes() != null) this.notes = invoiceView.getNotes();
        if (invoiceView.getTotalPrice() != null) this.totalPrice = invoiceView.getTotalPrice();
        if (creator != null) this.createdBy = creator;
        if (client != null) this.client = client;
        if (invoiceView.getPaymentMethod() != null) this.paymentMethod = invoiceView.getPaymentMethod();
        if (invoiceView.getWarrantyPeriod() != null) {
            this.warrantyPeriod = invoiceView.getWarrantyPeriod();
        } else {
            this.warrantyPeriod = WarrantyPeriod.NONE;
        }
    }

    public boolean isValid(){
        return this.valid==null || this.valid;
    }
}
