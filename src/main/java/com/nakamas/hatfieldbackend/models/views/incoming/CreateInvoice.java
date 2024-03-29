package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.PaymentMethod;
import com.nakamas.hatfieldbackend.models.enums.WarrantyPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInvoice {
    private InvoiceType type;
    private String deviceName;
    private String serialNumber;
    private Integer count;
    private ZonedDateTime timestamp;

    private Long ticketId;
    private Long itemId;
    private String itemName;
    private String notes;
    private BigDecimal totalPrice;
    private UUID clientId;
    private PaymentMethod paymentMethod;
    private WarrantyPeriod warrantyPeriod;

    public void setTicketInfo(Ticket ticket) {
        this.ticketId = ticket.getId();
        if (this.deviceName == null) this.deviceName = "-";
        if (this.serialNumber == null) this.serialNumber = "-";
    }

    public void setItemInfo(InventoryItem item) {
        this.itemId = item.getId();
        this.deviceName = item.getName();
        if (this.totalPrice == null) this.totalPrice = item.getPurchasePrice();
        this.itemName = item.getName();
    }
}
