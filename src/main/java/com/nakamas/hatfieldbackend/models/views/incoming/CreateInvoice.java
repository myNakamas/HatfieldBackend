package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.PaymentMethod;
import com.nakamas.hatfieldbackend.models.enums.WarrantyPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInvoice {
    private InvoiceType type;
    private Model deviceModel;
    private Brand deviceBrand;
    private String serialNumber;
    private User client;
    private String notes;
    private BigDecimal totalPrice;
    private User createdBy;
    private PaymentMethod paymentMethod;
    private WarrantyPeriod warranty;

    public void setTicketInfo(Ticket ticket){
        this.deviceModel = ticket.getDeviceModel();
        this.deviceBrand = ticket.getDeviceBrand();
        this.serialNumber = ticket.getSerialNumberOrImei();
        this.client = ticket.getClient();
        if(totalPrice!=null) this.totalPrice = ticket.getTotalPrice();
    }
}
