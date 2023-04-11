package com.nakamas.hatfieldbackend.models.views.incoming;

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
    private String notes;
    private BigDecimal totalPrice;
    private UUID createdBy;
    private UUID client;
    private PaymentMethod paymentMethod;
    private WarrantyPeriod warrantyPeriod;

    public void setTicketInfo(Ticket ticket){
        this.ticketId = ticket.getId();
        this.type = InvoiceType.REPAIR;
        this.deviceModel = ticket.getDeviceModel().getModel();
        this.deviceBrand = ticket.getDeviceBrand().getBrand();
        this.serialNumber = ticket.getSerialNumberOrImei();
        this.client = ticket.getClient().getId();
        if(totalPrice!=null) this.totalPrice = ticket.getTotalPrice();
    }
}
