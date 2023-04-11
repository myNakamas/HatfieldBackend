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
    @ManyToOne
    private Model deviceModel;
    @ManyToOne
    private Brand deviceBrand;
    private String serialNumber;
    private Long ticketId;
    private ZonedDateTime timestamp;
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


    public Invoice(CreateInvoice invoiceView){
        this.type = invoiceView.getType();
        this.deviceModel = invoiceView.getDeviceModel();
        this.deviceBrand = invoiceView.getDeviceBrand();
        this.serialNumber = invoiceView.getSerialNumber();
        this.ticketId = invoiceView.getTicketId();
        this.timestamp = ZonedDateTime.now();
        this.notes = invoiceView.getNotes();
        this.totalPrice = invoiceView.getTotalPrice();
        this.createdBy = invoiceView.getCreatedBy();
        this.client = invoiceView.getClient();
        this.paymentMethod = invoiceView.getPaymentMethod();
        this.warrantyPeriod = invoiceView.getWarranty();
    }
}
