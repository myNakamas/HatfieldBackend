package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Ticket extends AbstractPersistable<Long> {
    private Model deviceModel;
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
    @Column(columnDefinition = "text")
    private String notes;
    private BigDecimal totalPrice;
    private BigDecimal deposit;

    @ManyToOne
    private User createdBy;
    @ManyToOne
    private User client;

    @ManyToOne
    private Shop shop;

    @Enumerated
    private TicketStatus status;

    @OneToMany(mappedBy = "ticket")
    private List<UsedPart> usedParts;

    @OneToMany(mappedBy = "ticket")
    private List<ChatMessage> chatMessages;



}
