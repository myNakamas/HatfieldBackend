package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Part;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
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
    private String userRequest;
    @Column(columnDefinition = "text")
    private String deviceProblemExplanation;
    private String deviceCondition;
    private Boolean deviceIsLeftInStore;
    private String devicePassword;
    private String serialNumberOrImei;
    private String accessories;
    private LocalDateTime timestamp;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private Shop shop;

    @Enumerated
    private TicketStatus status;
    private BigDecimal totalPrice;
    private BigDecimal deposit;

    @OneToMany
    @JoinColumn(name = "ticket_id")
    private List<Part> usedParts;

    @OneToMany
    @JoinColumn(name = "ticket_id")
    private List<ChatMessage> chatMessages;



}
