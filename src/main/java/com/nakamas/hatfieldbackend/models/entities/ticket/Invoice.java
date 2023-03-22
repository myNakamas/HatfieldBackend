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
    @ManyToOne
    private Model deviceModel;
    @ManyToOne
    private Brand deviceBrand;
    private String serialNumber;
    private LocalDateTime timestamp;
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
        this.type = invoiceView.type();
        this.deviceModel = invoiceView.deviceModel();
        this.deviceBrand = invoiceView.deviceBrand();
        this.serialNumber = invoiceView.serialNumber();
        this.timestamp = LocalDateTime.now();
        this.notes = invoiceView.notes();
        this.totalPrice = invoiceView.totalPrice();
        this.createdBy = invoiceView.createdBy();
        this.client = invoiceView.client();
        this.paymentMethod = invoiceView.paymentMethod();
        this.warrantyPeriod = invoiceView.warranty();
    }
}
