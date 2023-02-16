package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_table")
@Entity
public class User extends AbstractPersistable<UUID> {
    @Column(unique = true)
    private String username;
    private String fullName;
    private String password;
    @Column(unique = true)
    private String email;
    @ElementCollection
    private List<String> phones;
    @Enumerated
    private UserRole role;
    private Boolean isActive;
    private Boolean isBanned;

    private Boolean smsPermission = true;
    private Boolean emailPermission = true;

    @ManyToOne
    private Shop shop;
    @OneToMany(mappedBy = "receiver")
    private List<ChatMessage> receivedMessages;
    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> sentMessages;
    @OneToMany(mappedBy = "createdBy")
    private List<Invoice> createdInvoices;
    @OneToMany(mappedBy = "client")
    private List<Invoice> clientInvoices;
    @OneToMany(mappedBy = "createdBy")
    private List<Ticket> createdTickets;
    @OneToMany(mappedBy = "client")
    private List<Ticket> clientTickets;

    private void generateUsername(){
        this.username = "generated LOL";
    }
}
