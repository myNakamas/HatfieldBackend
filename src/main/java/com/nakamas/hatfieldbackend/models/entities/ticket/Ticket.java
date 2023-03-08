package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.services.listeners.TicketListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
@EntityListeners(TicketListener.class)
public class Ticket extends AbstractPersistable<Long> {
    @ManyToOne
    private Model deviceModel;
    @ManyToOne
    private Brand deviceBrand;
    @Column(columnDefinition = "text")
    private String customerRequest;
    @Column(columnDefinition = "text")
    private String deviceProblemExplanation;
    private String deviceCondition;
    //todo: add relation to table deviceLocation
    private String devicePassword;
    private String serialNumberOrImei;
    private String accessories;
    private LocalDateTime timestamp;
//    manually set
    private LocalDateTime deadline;
    @Column(columnDefinition = "text")
    private String notes;
    private BigDecimal totalPrice;
    private BigDecimal deposit;
    private Integer priority = 0;

    @ManyToOne
    private User createdBy;
    @ManyToOne
    private User client;
    @ManyToOne
    private Shop shop;

    @Enumerated
    private TicketStatus status;

    @OneToMany(mappedBy = "ticket")
    private List<UsedPart> usedParts = new ArrayList<>();

    @OneToMany(mappedBy = "ticket")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public Ticket(CreateTicket create, User user) {
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
        this.priority = create.priority();
        this.status = create.status();

        this.createdBy = user;
        this.shop = user.getShop();

        this.timestamp = LocalDateTime.now();
    }
}
