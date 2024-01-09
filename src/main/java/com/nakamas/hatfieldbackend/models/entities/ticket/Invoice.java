package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.PaymentMethod;
import com.nakamas.hatfieldbackend.models.enums.WarrantyPeriod;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Table
@Entity
public class Invoice extends AbstractPersistable<Long> {
    @Enumerated
    private InvoiceType type;
    private String deviceName;
    private String serialNumber;
    @Column(name = "ticket_id")
    private Long ticketId;
    @Column(name = "item_id")
    private Long itemId;
    private ZonedDateTime timestamp;
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
    private Boolean valid;

    public Invoice() {
        this.valid = true;
    }

    public Invoice(CreateInvoice invoiceView, User creator, User client) {
        this();
        this.timestamp = invoiceView.getTimestamp() != null ? invoiceView.getTimestamp() : ZonedDateTime.now();
        if (invoiceView.getType() != null) this.type = invoiceView.getType();
        if (invoiceView.getDeviceName() != null) this.deviceName = invoiceView.getDeviceName();
        if (invoiceView.getSerialNumber() != null && !invoiceView.getSerialNumber().isBlank())
            this.serialNumber = invoiceView.getSerialNumber();
        else this.serialNumber = "-";
        if (invoiceView.getTicketId() != null) this.ticketId = invoiceView.getTicketId();
        if (invoiceView.getItemId() != null) this.itemId = invoiceView.getItemId();
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

    public Invoice(Ticket ticket, InvoiceType type, User creator) {
        this.type = type;
        this.serialNumber = ticket.getSerialNumberOrImei();
        this.deviceName = "%s %s".formatted(ticket.getDeviceBrandString(), ticket.getDeviceModelString());
        this.client = ticket.getClient();
        this.createdBy = creator;
        this.count = 1;
    }

    public boolean isValid() {
        return this.valid == null || this.valid;
    }

    public static boolean isTicketInvoice(Invoice i) {
        return i.isValid() && i.getType().equals(InvoiceType.REPAIR);
    }
}
