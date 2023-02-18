package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_table")
@Entity
public class User extends AbstractPersistable<UUID> implements UserDetails {
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

    private void generateLoginData(){
        this.username = "generated LOL";
        this.password = "generated LOL";
    }

    public User (CreateUser user){
        this.username = user.username();
        this.fullName = user.fullName();
        this.password = user.password();
        this.email = user.email();
        this.phones = user.phones();
        this.role = user.role();
        this.isActive = true;
        this.isBanned = false;
        if(Objects.equals(user.role(), UserRole.CLIENT)){
            this.generateLoginData();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
