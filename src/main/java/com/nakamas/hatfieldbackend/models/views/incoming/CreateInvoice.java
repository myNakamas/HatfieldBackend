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
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInvoice {
    private InvoiceType type;
    private String deviceModel;
    private String deviceBrand;
    private String serialNumber;
    private Integer count;

    private Long ticketId;
    private Long itemId;
    private String itemName;
    private String notes;
    private BigDecimal totalPrice;
    private UUID clientId;
    private PaymentMethod paymentMethod;
    private WarrantyPeriod warrantyPeriod;

    public void setTicketInfo(Ticket ticket){
        this.ticketId = ticket.getId();
        this.type = InvoiceType.REPAIR;
        this.deviceModel = ticket.getDeviceModelString();
        this.deviceBrand = ticket.getDeviceBrandString();
        this.serialNumber = ticket.getSerialNumberOrImei();
        if (ticket.getClient() != null) this.clientId = ticket.getClient().getId();
        if (ticket.getTotalPrice() != null) this.totalPrice = ticket.getTotalPrice();
    }
    public void setItemInfo(InventoryItem item){
        this.itemId = item.getId();
        this.deviceModel = item.getModelString();
        this.deviceBrand = item.getBrandString();
        if (this.totalPrice == null) this.totalPrice = item.getPrice();
        this.itemName = item.getName();
    }
}
