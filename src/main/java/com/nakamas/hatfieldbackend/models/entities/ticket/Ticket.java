package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table
@Entity
public class Ticket extends AbstractPersistable<Long> {
    @ManyToOne
    private Model deviceModel;
    @ManyToOne
    private Brand deviceBrand;
    @ManyToOne
    private DeviceLocation deviceLocation;
    @Column(columnDefinition = "text")
    private String customerRequest;
    @Column(columnDefinition = "text")
    private String deviceProblemExplanation;
    private String deviceCondition;
    private String devicePassword;
    private String serialNumberOrImei;
    private String accessories;
    private ZonedDateTime timestamp;
    //    manually set
    private ZonedDateTime deadline;
    @Column(columnDefinition = "text")
    private String notes;
    private BigDecimal totalPrice;
    private BigDecimal deposit = BigDecimal.ZERO;

    @ManyToOne
    private User createdBy;
    @ManyToOne
    private User client;
    @ManyToOne
    private Shop shop;
    @OneToMany()
    @OrderBy("id desc")
    @JoinColumn(name = "ticket_id")
    private List<Invoice> invoices = new ArrayList<>();

    @Enumerated
    private TicketStatus status;

    public String getDeviceModelString() {
        return deviceModel != null ? deviceModel.getModel() : "";
    }

    public String getDeviceBrandString() {
        return deviceBrand != null ? deviceBrand.getBrand() : "";
    }

    public String getDeviceLocationString() {
        return deviceLocation != null ? deviceLocation.getLocation() : "";
    }

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<UsedPart> usedParts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public Ticket() {
        this.timestamp = ZonedDateTime.now();
    }

    public Ticket(CreateTicket create, User user) {
        this();
        this.customerRequest = create.customerRequest();
        this.deviceProblemExplanation = create.problemExplanation();
        this.deviceCondition = create.deviceCondition();
        this.devicePassword = create.devicePassword();
        this.serialNumberOrImei = create.serialNumberOrImei();
        this.accessories = create.accessories();
        this.deadline = create.deadline();
        this.notes = create.notes();
        this.totalPrice = create.totalPrice();
        this.deposit = create.deposit();
        this.status = create.status();
        this.createdBy = user;
        this.shop = user.getShop();
    }

    public void update(CreateTicket ticket) {
        if (ticket.customerRequest() != null) this.customerRequest = ticket.customerRequest();
        if (ticket.problemExplanation() != null) this.deviceProblemExplanation = ticket.problemExplanation();
        if (ticket.deviceCondition() != null) this.deviceCondition = ticket.deviceCondition();
        if (ticket.devicePassword() != null) this.devicePassword = ticket.devicePassword();
        if (ticket.serialNumberOrImei() != null) this.serialNumberOrImei = ticket.serialNumberOrImei();
        if (ticket.accessories() != null) this.accessories = ticket.accessories();
        if (ticket.deadline() != null) this.deadline = ticket.deadline();
        if (ticket.notes() != null) this.notes = ticket.notes();
        if (ticket.totalPrice() != null) this.totalPrice = ticket.totalPrice();
        if (ticket.deposit() != null) this.deposit = ticket.deposit();
        if (ticket.status() != null) this.status = ticket.status();
    }
}
